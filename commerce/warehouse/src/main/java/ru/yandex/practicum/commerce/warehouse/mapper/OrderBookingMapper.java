package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;

@Mapper(componentModel = "spring")
public interface OrderBookingMapper {

    @Mapping(target = "deliveryWeight", source = "deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "deliveryVolume")
    @Mapping(target = "fragile", source = "fragile")
    BookedProductsDto toDto(OrderBooking booking);

    @Mapping(target = "deliveryWeight", source = "deliveryWeight", defaultValue = "0.0")
    @Mapping(target = "deliveryVolume", source = "deliveryVolume", defaultValue = "0.0")
    @Mapping(target = "fragile", source = "fragile", defaultValue = "false")
    OrderBooking toEntity(BookedProductsDto dto);
}