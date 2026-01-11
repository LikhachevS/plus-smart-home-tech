package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.DimensionDto;

import java.util.UUID;

@Data
@Builder
public class NewProductInWarehouseRequest {

    @NotBlank
    private UUID productId;

    @NotNull
    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(1)
    private double weight;
}