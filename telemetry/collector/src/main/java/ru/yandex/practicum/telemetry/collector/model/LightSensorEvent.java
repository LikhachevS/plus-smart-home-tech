package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class LightSensorEvent extends SensorEvent {

    @NotNull(message = "Качество связи обязательно")
    private int linkQuality;

    @NotNull(message = "Уровень освещённости обязателен")
    private int luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}