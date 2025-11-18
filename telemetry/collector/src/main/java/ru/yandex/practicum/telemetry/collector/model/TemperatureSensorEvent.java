package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class TemperatureSensorEvent extends SensorEvent {

    @NotNull(message = "Температура в °C обязательна")
    private int temperatureC;

    @NotNull(message = "Температура в °F обязательна")
    private int temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}