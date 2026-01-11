package ru.yandex.practicum.commerce.interaction_api.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductReturnRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private Map<UUID, Integer> products;
}