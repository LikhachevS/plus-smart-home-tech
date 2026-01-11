package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interaction_api.order.client.OrderClient;
import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.*;
import ru.yandex.practicum.commerce.interaction_api.warehouse.exception.ProductLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.Warehouse;
import ru.yandex.practicum.commerce.warehouse.exception.*;
import ru.yandex.practicum.commerce.warehouse.mapper.OrderBookingMapper;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductInWarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.ProductInWarehouse;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;
    private final OrderBookingRepository orderBookingRepository;
    private final ProductInWarehouseMapper mapper;
    private final OrderBookingMapper orderBookingMapper;
    private final OrderClient orderClient;

    @Override
    public ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        if (isProductInWarehouse(newProductInWarehouseRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт с id " +
                    newProductInWarehouseRequest.getProductId() + " уже добавлен на склад!");
        }

        ProductInWarehouse entity = mapper.toEntity(newProductInWarehouseRequest);
        ProductInWarehouse savedEntity = repository.save(entity);

        return mapper.toDto(savedEntity);
    }

    @Override
    public BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart) {
        BookedProductsDto bookedProductsDto = BookedProductsDto.builder().build();

        shoppingCart.getProducts().forEach((productId, quantity) -> {
            ProductInWarehouse productInWarehouse = productInWarehouseExists(productId);

            if (quantity > productInWarehouse.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Товара с id " +
                        productId + " в корзине больше, чем доступно на складе!");
            }

            bookedProductsDto.setDeliveryWeight(bookedProductsDto.getDeliveryWeight() + productInWarehouse.getWeight());
            bookedProductsDto.setDeliveryVolume(bookedProductsDto.getDeliveryVolume() + calculateVolume(productInWarehouse));
        });

        return bookedProductsDto;
    }

    @Override
    public void acceptProduct(AddProductToWarehouseRequest request) {
        ProductInWarehouse productInWarehouse = productInWarehouseExists(request.getProductId());
        productInWarehouse.setQuantity(productInWarehouse.getQuantity() + request.getQuantity());

        repository.save(productInWarehouse);
    }

    @Override
    public AddressDto getAddress() {
        return Warehouse.getRandomAddress();

    }

    @Override
    public void shippedProducts(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotOrderBookingFoundException("Забронированные товары для заказа с id " +
                        request.getOrderId() + " не найдены!"));

        orderBooking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooking);

        log.info("Товары для заказа с id {} переданы в доставку!", request.getOrderId());
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) {

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductInWarehouse product = productInWarehouseExists(productId);

                product.setQuantity(product.getQuantity() + quantity);
                repository.save(product);

            } catch (ProductInWarehouseNotFoundException e) {
                log.warn("Продукт с id {} не найден на складе, пропускаем!", productId);
            }
        }

        log.info("Товары успешно вернулись на склад!");
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {

        Double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Integer> entry : request.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductInWarehouse product = productInWarehouseExists(productId);

                if (product.getQuantity() < quantity) {
                    throw new ProductLowQuantityInWarehouseException("Товара с id " + productId +
                            " на складе меньше, чем запрашивается!");
                }

                product.setQuantity(product.getQuantity() - quantity);
                repository.save(product);

                log.info("Остаток товара с id: {} на складе: {}.", productId, product.getQuantity().toString());

                deliveryWeight += product.getWeight();
                deliveryVolume += calculateVolume(product);

                if (product.getFragile()) {
                    fragile = true;
                }

            } catch (ProductInWarehouseNotFoundException e) {
                throw new SpecifiedProductAlreadyInWarehouseException("Товар с id " + productId + " не найден на складе!");
            }
        }

        OrderBooking newOrderBooking = OrderBooking.builder()
                .products(request.getProducts())
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();

        orderClient.assemblyOrder(request.getOrderId());

        return orderBookingMapper.toDto(orderBookingRepository.save(newOrderBooking));
    }

    private boolean isProductInWarehouse(UUID productId) {
        return repository.existsById(productId);
    }

    private ProductInWarehouse productInWarehouseExists(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ProductInWarehouseNotFoundException("Продукт с id " + productId + " не найден на складе!"));
    }

    private Double calculateVolume(ProductInWarehouse product) {
        DimensionDto dimension = product.getDimension();
        return dimension.getHeight() * dimension.getDepth() * dimension.getWidth();
    }
}