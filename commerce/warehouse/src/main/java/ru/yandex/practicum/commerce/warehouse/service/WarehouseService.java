package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.ProductInWarehouseDto;
import ru.yandex.practicum.commerce.warehouse.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.NewProductInWarehouseRequest;

public interface WarehouseService {

    ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart);

    void acceptProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}