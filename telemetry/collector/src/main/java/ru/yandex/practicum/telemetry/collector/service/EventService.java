package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface EventService {

    void sendSensorEvent(SensorEventAvro event);

    void sendHubEvent(HubEventAvro event);
}