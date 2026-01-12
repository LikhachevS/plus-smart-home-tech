package ru.yandex.practicum.commerce.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.interaction_api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.interaction_api.order.dto.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {
    Page<OrderDto> getOrder(String username, Pageable pageable);

    OrderDto createNewOrder(String username, CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto paymentOrder(UUID orderId);

    OrderDto failedPaymentOrder(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto failedDeliveryOrder(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotalOrder(UUID orderId);

    OrderDto calculateDeliveryOrder(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto failedAssemblyOrder(UUID orderId);

    OrderDto getOrderByPayment(UUID paymentId);

    OrderDto getOrderByDelivery(UUID deliveryId);
}