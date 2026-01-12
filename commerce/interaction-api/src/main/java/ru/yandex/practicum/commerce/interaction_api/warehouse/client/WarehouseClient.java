package ru.yandex.practicum.commerce.interaction_api.warehouse.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.interaction_api.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProductForOrderFromShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();

    @PostMapping("/api/v1/warehouse/returnProducts")
    void returnProducts(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProducts(@RequestBody @Valid AssemblyProductsForOrderRequest request);

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedOrder(@RequestBody @Valid ShippedToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCart);
}