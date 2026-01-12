package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.model.DeliveryAddress;
import ru.yandex.practicum.commerce.interaction_api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(source = "fromAddress", target = "fromAddress")
    @Mapping(source = "toAddress", target = "toAddress")
    DeliveryDto toDto(Delivery entity);

    @Mapping(source = "fromAddress", target = "fromAddress")
    @Mapping(source = "toAddress", target = "toAddress")
    Delivery toEntity(DeliveryDto dto);

    @Named("addressToDto")
    AddressDto addressToDto(DeliveryAddress address);

    @Named("dtoToAddress")
    DeliveryAddress dtoToAddress(AddressDto dto);
}