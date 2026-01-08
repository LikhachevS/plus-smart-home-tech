package ru.yandex.practicum.commerce.shopping_store.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.commerce.interaction_api.shopping_store.dto.ProductDto;
import ru.yandex.practicum.commerce.shopping_store.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product product);
}