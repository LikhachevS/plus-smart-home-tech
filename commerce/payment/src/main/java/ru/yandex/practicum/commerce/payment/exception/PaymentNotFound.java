package ru.yandex.practicum.commerce.payment.exception;

public class PaymentNotFound extends RuntimeException {
    public PaymentNotFound(String message) {
        super(message);
    }
}