package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {

    @NotBlank(message = "Идентификатор датчика не может быть пустым")
    private String sensorId;

    @NotBlank(message = "Тип действия не может быть пустым")
    private ActionType type;

    private Integer value; // Необязательное поле
}