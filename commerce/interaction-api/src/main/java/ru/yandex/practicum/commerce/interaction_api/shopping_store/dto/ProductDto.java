package ru.yandex.practicum.commerce.interaction_api.shopping_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.ProductState;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.QuantityState;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDto {

    private String productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @NotNull
    private ProductCategory productCategory;

    @NotNull
    @Min(1)
    private BigDecimal price;
}