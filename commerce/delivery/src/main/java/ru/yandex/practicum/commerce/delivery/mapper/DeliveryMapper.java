package ru.yandex.practicum.commerce.delivery.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.model.DeliveryAddress;
import ru.yandex.practicum.commerce.interaction_api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.interaction_api.warehouse.dto.AddressDto;

@Component
public class DeliveryMapper {

    public DeliveryDto toDto(Delivery entity) {
        if (entity == null) return null;

        return DeliveryDto.builder()
                .deliveryId(entity.getDeliveryId())
                .orderId(entity.getOrderId())
                .deliveryState(entity.getDeliveryState())
                .fromAddress(addressToDto(entity.getFromAddress()))
                .toAddress(addressToDto(entity.getToAddress()))
                .build();
    }

    public Delivery toEntity(DeliveryDto dto) {
        if (dto == null) return null;

        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState())
                .fromAddress(dtoToAddress(dto.getFromAddress()))
                .toAddress(dtoToAddress(dto.getToAddress()))
                .build();
    }

    private AddressDto addressToDto(DeliveryAddress address) {
        if (address == null) return null;

        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    private DeliveryAddress dtoToAddress(AddressDto dto) {
        if (dto == null) return null;

        return DeliveryAddress.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }
}