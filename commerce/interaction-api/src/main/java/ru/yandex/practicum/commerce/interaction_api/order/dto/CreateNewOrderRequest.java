package ru.yandex.practicum.commerce.interaction_api.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateNewOrderRequest {

    @NotNull
    private String username; //Добавил поле

    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}