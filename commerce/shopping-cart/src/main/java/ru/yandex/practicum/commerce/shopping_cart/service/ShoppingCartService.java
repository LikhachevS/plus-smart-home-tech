package ru.yandex.practicum.commerce.shopping_cart.service;

import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.shopping_cart.model.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProductToCart(String username, Map<String, Integer> products);

    void deactivateCart(String username);

    ShoppingCartDto removeProductFromCart(String username, List<String> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}