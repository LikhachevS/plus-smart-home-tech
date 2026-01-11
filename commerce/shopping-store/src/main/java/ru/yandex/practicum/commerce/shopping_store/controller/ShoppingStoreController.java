package ru.yandex.practicum.commerce.shopping_store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.dto.ProductDto;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.ProductCategory;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.QuantityState;
import ru.yandex.practicum.commerce.shopping_store.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.shopping_store.service.ShoppingStoreService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {

    private final ShoppingStoreService service;

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return service.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable String productId) {
        return service.getProductById(productId);
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        return service.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody String productId) {
        return service.removeProduct(productId.replaceAll("^\"|\"$", ""));
    }

    @PostMapping("/quantityState")
    public Boolean setQuantity(@RequestParam String productId, @RequestParam QuantityState quantityState) {
        return service.setQuantity(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }
}