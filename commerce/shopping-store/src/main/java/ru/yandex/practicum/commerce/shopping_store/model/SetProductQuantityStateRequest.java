package ru.yandex.practicum.commerce.shopping_store.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.QuantityState;

@Data
@Builder
public class SetProductQuantityStateRequest {

    @NotBlank
    private String productId;

    @NotNull
    private QuantityState quantityState;
}