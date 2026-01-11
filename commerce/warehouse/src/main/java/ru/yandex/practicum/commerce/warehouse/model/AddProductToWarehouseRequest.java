package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddProductToWarehouseRequest {

    @NotBlank
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}