package ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ShoppingCartDto {

    @NotBlank
    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, Integer> products;
}