package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.DimensionDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.ProductInWarehouseDto;
import ru.yandex.practicum.commerce.warehouse.Warehouse;
import ru.yandex.practicum.commerce.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.exception.ProductInWarehouseNotFoundException;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductInWarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.ProductInWarehouse;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;
    private final ProductInWarehouseMapper mapper;

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

    private boolean isProductInWarehouse(String productId) {
        return repository.existsById(productId);
    }

    private ProductInWarehouse productInWarehouseExists(String productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ProductInWarehouseNotFoundException("Продукт с id " + productId + " не найден на складе!"));
    }

    private Double calculateVolume(ProductInWarehouse product) {
        DimensionDto dimension = product.getDimension();
        return dimension.getHeight() * dimension.getDepth() * dimension.getWidth();
    }
}