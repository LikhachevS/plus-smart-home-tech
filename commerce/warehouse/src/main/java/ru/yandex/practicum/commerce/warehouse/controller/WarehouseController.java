package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.*;
import ru.yandex.practicum.commerce.warehouse.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService service;

    @GetMapping("/address")
    public AddressDto getAddress() {
        return service.getAddress();
    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCart) {
        return service.checkQuantityForCart(shoppingCart);
    }

    @PutMapping
    public ProductInWarehouseDto addNewProduct(@RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest) {
        return service.addNewProduct(newProductInWarehouseRequest);
    }

    @PostMapping("/add")
    public void acceptProduct(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.acceptProduct(request);
    }

    @PostMapping("/shipped")
    public void shippedOrder(@RequestBody @Valid ShippedToDeliveryRequest request) {
        service.shippedProducts(request);
    }

    @PostMapping("/returnProducts")
    public void returnProducts(@RequestBody @Valid Map<UUID, Integer> products) {
        service.returnProducts(products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(@RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProducts(request);
    }
}