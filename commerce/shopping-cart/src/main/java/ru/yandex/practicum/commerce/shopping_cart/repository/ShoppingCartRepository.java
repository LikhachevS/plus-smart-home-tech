package ru.yandex.practicum.commerce.shopping_cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shopping_cart.model.Cart;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByOwner(String owner);
}