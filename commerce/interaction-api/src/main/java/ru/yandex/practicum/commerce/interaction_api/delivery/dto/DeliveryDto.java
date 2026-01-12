package ru.yandex.practicum.commerce.interaction_api.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction_api.delivery.enums.DeliveryState;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;

import java.util.UUID;

@Data
@Builder
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull
    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    @Builder.Default
    private DeliveryState deliveryState = DeliveryState.CREATED;
}