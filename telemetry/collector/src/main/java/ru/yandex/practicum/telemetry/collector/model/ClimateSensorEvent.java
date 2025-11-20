package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class ClimateSensorEvent extends SensorEvent {

    @NotNull(message = "Температура в °C обязательна")
    private int temperatureC;

    @NotNull(message = "Уровень влажности обязателен")
    private int humidity;

    @NotNull(message = "Уровень CO₂ обязателен")
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}