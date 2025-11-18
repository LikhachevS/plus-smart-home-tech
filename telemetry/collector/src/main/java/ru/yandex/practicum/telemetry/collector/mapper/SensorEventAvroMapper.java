package ru.yandex.practicum.telemetry.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.*;

@Component
public class SensorEventAvroMapper {

    public SensorEventAvro toAvro(SensorEvent event) {

        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp());

        switch (event.getType()) {
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climateEvent = (ClimateSensorEvent) event;
                ClimateSensorAvro climateAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climateEvent.getTemperatureC())
                        .setHumidity(climateEvent.getHumidity())
                        .setCo2Level(climateEvent.getCo2Level())
                        .build();
                builder.setPayload(climateAvro);
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent lightEvent = (LightSensorEvent) event;
                LightSensorAvro lightAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(lightEvent.getLinkQuality())
                        .setLuminosity(lightEvent.getLuminosity())
                        .build();
                builder.setPayload(lightAvro);
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motionEvent = (MotionSensorEvent) event;
                MotionSensorAvro motionAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(motionEvent.getLinkQuality())
                        .setMotion(motionEvent.isMotion())
                        .setVoltage(motionEvent.getVoltage())
                        .build();
                builder.setPayload(motionAvro);
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchEvent = (SwitchSensorEvent) event;
                SwitchSensorAvro switchAvro = SwitchSensorAvro.newBuilder()
                        .setState(switchEvent.isState())
                        .build();
                builder.setPayload(switchAvro);
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
                TemperatureSensorAvro tempAvro = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(tempEvent.getTemperatureC())
                        .setTemperatureF(tempEvent.getTemperatureF())
                        .build();
                builder.setPayload(tempAvro);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported SensorEventType: " + event.getType());
        }

        return builder.build();
    }
}