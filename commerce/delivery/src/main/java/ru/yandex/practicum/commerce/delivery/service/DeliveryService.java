package ru.yandex.practicum.commerce.delivery.service;

import ru.yandex.practicum.commerce.interaction_api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto delivery);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal calculateDeliveryCost(OrderDto order);
}