package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interaction_api.order.client.OrderClient;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.interaction_api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.interaction_api.payment.enums.PaymentStatus;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.dto.ProductDto;
import ru.yandex.practicum.commerce.payment.exception.PaymentNotFound;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private final BigDecimal TAX = BigDecimal.valueOf(0.1);

    @Override
    public PaymentDto goToPayment(OrderDto order) {
        // 1. Валидация входных данных
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getProductPrice() == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (order.getDeliveryPrice() == null) {
            throw new IllegalArgumentException("Delivery price cannot be null");
        }

        // Проверка на неотрицательность
        if (order.getProductPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (order.getDeliveryPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Delivery price cannot be negative");
        }

        // 2. Расчёт стоимости товаров с учётом НДС (10%)
        BigDecimal taxRate = BigDecimal.valueOf(0.1); // 10% НДС
        BigDecimal feeTotal = order.getProductPrice().multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP); // НДС (feeTotal)

        BigDecimal productCostWithTax = order.getProductPrice().add(feeTotal)
                .setScale(2, RoundingMode.HALF_UP); // Стоимость товаров с НДС

        // 3. Расчёт итоговой суммы (товары с НДС + доставка)
        BigDecimal totalPayment = productCostWithTax.add(order.getDeliveryPrice())
                .setScale(2, RoundingMode.HALF_UP);

        // 4. Создание сущности Payment с расчётанными значениями
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .totalPayment(totalPayment)
                .totalProduct(order.getProductPrice())
                .deliveryTotal(order.getDeliveryPrice())
                .feeTotal(feeTotal)
                .status(PaymentStatus.PENDING)
                .build();

        // 5. Сохранение платежа в БД
        Payment savedPayment = repository.save(payment);

        // 6. Преобразование в DTO для возврата клиенту
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentId(savedPayment.getPaymentId())
                .totalPayment(savedPayment.getTotalPayment())
                .deliveryTotal(savedPayment.getDeliveryTotal())
                .feeTotal(savedPayment.getFeeTotal())
                .build();

        // 7. Логирование
        log.info("Платёж создан. ID: {}, сумма: {}",
                savedPayment.getPaymentId(), savedPayment.getTotalPayment());

        return paymentDto;
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto order) {
        if (order.getDeliveryPrice() == null) {
            throw new IllegalArgumentException("Delivery price cannot be null");
        }
        if (order.getProductPrice() == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }

        if (order.getDeliveryPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Delivery price cannot be negative");
        }
        if (order.getProductPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        BigDecimal productCostWithTax = order.getProductPrice()
                .multiply(BigDecimal.ONE.add(TAX)) // TAX = 0.1 → 1.1
                .setScale(2, RoundingMode.HALF_UP); // округление до копеек

        BigDecimal totalCost = order.getDeliveryPrice()
                .add(productCostWithTax)
                .setScale(2, RoundingMode.HALF_UP);

        return totalCost;
    }

    @Override
    public void createRefund(UUID paymentId) {

        Payment payment = getPayment(paymentId);

        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        OrderDto order = orderClient.getOrderByPayment(paymentId);
        orderClient.paymentOrder(order.getOrderId());

        log.info("Оплата с id {} прошла успешно!", paymentId);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto order) {
        Map<UUID, Integer> products = order.getProducts();
        BigDecimal productCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductDto product = shoppingStoreClient.getProductById(productId);

                productCost = productCost.add(
                        product.getPrice().multiply(BigDecimal.valueOf(quantity))
                );

            } catch (RuntimeException e) {
                log.warn("Продукт с id: {} не найден, пропускаю.", productId);
            }
        }

        return productCost;
    }

    @Override
    public void failedPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);

        payment.setStatus(PaymentStatus.FAILED);
        repository.save(payment);

        OrderDto order = orderClient.getOrderByPayment(paymentId);
        orderClient.failedPaymentOrder(order.getOrderId());

        log.warn("Ошибка при оплате с id {}!", paymentId);
    }

    private Payment getPayment(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFound("Оплата с id " + paymentId + " не найдена!"));
    }
}