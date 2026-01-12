package ru.yandex.practicum.commerce.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction_api.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.interaction_api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderDto> getOrder(
            @RequestParam String username,
            @PageableDefault Pageable pageable
    ) {
        log.info("Запрос на получение заказов пользователя: {}", username);
        return orderService.getOrder(username, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public OrderDto createNewOrder(@RequestParam String username,
                                   @RequestBody CreateNewOrderRequest request
    ) {
        log.info("Запрос на создание нового заказа: {}", request);
        return orderService.createNewOrder(username, request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request) {
        log.info("Запрос на возврат заказа: {}", request);
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody UUID orderId) {
        log.info("Запрос на оплату заказа: {}", orderId);
        return orderService.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto failedPaymentOrder(@RequestBody UUID orderId) {
        log.info("Запрос при неудачной оплате заказа: {}", orderId);
        return orderService.failedPaymentOrder(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        log.info("Запрос при удачной доставке заказа: {}", orderId);
        return orderService.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto failedDeliveryOrder(@RequestBody UUID orderId) {
        log.info("Запрос при неудачной доставке заказа: {}", orderId);
        return orderService.failedDeliveryOrder(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody UUID orderId) {
        log.info("Запрос на завершение заказа: {}", orderId);
        return orderService.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder(@RequestBody UUID orderId) {
        log.info("Запрос на рассчет полной стоимости заказа: {}", orderId);
        return orderService.calculateTotalOrder(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryOrder(@RequestBody UUID orderId) {
        log.info("Запрос на рассчет стоимости доставки: {}", orderId);
        return orderService.calculateDeliveryOrder(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        log.info("Запрос на сбор заказа на складе: {}", orderId);
        return orderService.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto failedAssemblyOrder(@RequestBody UUID orderId) {
        log.info("Запрос при неудачной сборке заказа на складе: {}", orderId);
        return orderService.failedAssemblyOrder(orderId);
    }

    @PostMapping("/get/payment")
    public OrderDto getOrderByPayment(@RequestBody UUID paymentId) {
        return orderService.getOrderByPayment(paymentId);
    }

    @PostMapping("/get/delivery")
    public OrderDto getOrderByDelivery(@RequestBody UUID deliveryId) {
        return orderService.getOrderByDelivery(deliveryId);
    }
}