package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.*;
import ru.yandex.practicum.commerce.warehouse.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart);

    void acceptProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();

    void shippedProducts(ShippedToDeliveryRequest request);

    void returnProducts(Map<UUID, Integer> products);

    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request);
}