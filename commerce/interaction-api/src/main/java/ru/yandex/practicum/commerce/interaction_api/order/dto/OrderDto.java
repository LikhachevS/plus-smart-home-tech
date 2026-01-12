package ru.yandex.practicum.commerce.interaction_api.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.yandex.practicum.commerce.interaction_api.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID orderId;

    @NotBlank
    private UUID shoppingCartId;

    private Map<UUID, Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    private OrderState state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private BigDecimal totalPrice;

    private BigDecimal deliveryPrice;

    private BigDecimal productPrice;
}