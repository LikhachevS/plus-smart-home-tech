package ru.yandex.practicum.commerce.warehouse.exception;

public class NotOrderBookingFoundException extends RuntimeException {
    public NotOrderBookingFoundException(String message) {
        super(message);
    }
}