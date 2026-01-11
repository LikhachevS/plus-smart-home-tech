package ru.yandex.practicum.commerce.shopping_store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.dto.ProductDto;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.enums.ProductCategory;
import ru.yandex.practicum.commerce.shopping_store.model.SetProductQuantityStateRequest;

public interface ShoppingStoreService {

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(String productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProduct(String productId);

    Boolean setQuantity(SetProductQuantityStateRequest request);
}