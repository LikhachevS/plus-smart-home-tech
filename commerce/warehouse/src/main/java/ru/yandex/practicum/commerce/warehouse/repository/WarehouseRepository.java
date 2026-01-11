package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.warehouse.model.ProductInWarehouse;

public interface WarehouseRepository extends JpaRepository<ProductInWarehouse, String> {
}