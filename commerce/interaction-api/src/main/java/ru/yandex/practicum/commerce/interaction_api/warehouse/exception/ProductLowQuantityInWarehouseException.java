package ru.yandex.practicum.commerce.interaction_api.warehouse.exception;

public class ProductLowQuantityInWarehouseException extends RuntimeException {
    public ProductLowQuantityInWarehouseException(String message) {
        super(message);
    }
}