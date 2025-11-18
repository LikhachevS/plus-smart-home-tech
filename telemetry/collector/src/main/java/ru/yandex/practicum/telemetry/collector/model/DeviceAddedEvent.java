package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class DeviceAddedEvent extends HubEvent {

    @NotBlank(message = "ID устройства не может быть пустым")
    private String id;

    @NotNull(message = "Тип устройства обязателен")
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED; // Возвращаем значение перечисления
    }
}