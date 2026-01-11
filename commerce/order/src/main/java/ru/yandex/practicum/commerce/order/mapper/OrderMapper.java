package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction_api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.order.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);

    @Mapping(target = "username", ignore = true)
    Order toEntity(OrderDto orderDto);
}