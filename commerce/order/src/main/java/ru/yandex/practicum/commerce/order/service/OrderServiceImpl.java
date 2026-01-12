package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction_api.delivery.client.DeliveryClient;
import ru.yandex.practicum.commerce.interaction_api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.interaction_api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.interaction_api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.commerce.interaction_api.order.enums.OrderState;
import ru.yandex.practicum.commerce.interaction_api.payment.client.PaymentClient;
import ru.yandex.practicum.commerce.interaction_api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.client.WarehouseClient;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.order.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.order.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public Page<OrderDto> getOrder(String username, Pageable pageable) {
        validateUsername(username);

        Page<Order> orders = orderRepository.findByUsername(username, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        validateUsername(request.getUsername());

        Order newOrder = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .username(username)
                .build();

        Order order = orderRepository.save(newOrder);
        ;

        try {
            BookedProductsDto bookedProducts = warehouseClient.assemblyProducts(AssemblyProductsForOrderRequest.builder()
                    .products(request.getShoppingCart().getProducts())
                    .orderId(order.getOrderId())
                    .build());

            order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
            order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
            order.setFragile(bookedProducts.getFragile());

            order.setProductPrice(paymentClient.calculateProductCost(orderMapper.toDto(order)));

            DeliveryDto delivery = deliveryClient.createDelivery(
                    DeliveryDto.builder()
                            .fromAddress(warehouseClient.getAddress())
                            .toAddress(request.getDeliveryAddress())
                            .orderId(order.getOrderId())
                            .build()
            );
            order.setDeliveryId(delivery.getDeliveryId());

            BigDecimal deliveryPrice =
                    deliveryClient.calculateDeliveryCost(orderMapper.toDto(order));
            order.setDeliveryPrice(deliveryPrice);

            BigDecimal totalPrice =
                    paymentClient.calculateTotalCost(orderMapper.toDto(order));
            order.setTotalPrice(totalPrice);

            PaymentDto payment = paymentClient.goToPayment(orderMapper.toDto(order));
            order.setPaymentId(payment.getPaymentId());

            orderRepository.save(order);

            paymentClient.refund(payment.getPaymentId());

            return orderMapper.toDto(getOrder(order.getOrderId()));

        } catch (Exception e) {
            orderRepository.delete(order);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());

        warehouseClient.returnProducts(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto failedPaymentOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto failedDeliveryOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setTotalPrice(paymentClient.calculateTotalCost(orderMapper.toDto(order)));

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.toDto(order)));

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto failedAssemblyOrder(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto getOrderByPayment(UUID paymentId) {
        return orderMapper.toDto(orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id оплаты " + paymentId + " не найден!")));
    }

    @Override
    public OrderDto getOrderByDelivery(UUID deliveryId) {
        return orderMapper.toDto(orderRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id доставки " + deliveryId + " не найден!")));
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new NotAuthorizedUserException("Поле username не может быть пустым!");
        }
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id " + orderId + " не найден!"));
    }
}