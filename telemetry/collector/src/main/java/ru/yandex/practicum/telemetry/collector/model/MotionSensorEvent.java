package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class MotionSensorEvent extends SensorEvent {

    @NotNull(message = "Качество связи обязательно")
    private int linkQuality;

    @NotNull(message = "Статус движения обязателен")
    private boolean motion;

    @NotNull(message = "Напряжение обязательно")
    private int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}