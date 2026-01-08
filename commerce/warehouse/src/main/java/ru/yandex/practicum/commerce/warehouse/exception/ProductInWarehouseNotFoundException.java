package ru.yandex.practicum.commerce.warehouse.exception;

public class ProductInWarehouseNotFoundException extends RuntimeException {
    public ProductInWarehouseNotFoundException(String message) {
        super(message);
    }
}