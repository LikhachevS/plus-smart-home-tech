package ru.yandex.practicum.commerce.shopping_cart.exception;

public class CartWasDeactivatedException extends RuntimeException {
    public CartWasDeactivatedException(String message) {
        super(message);
    }
}