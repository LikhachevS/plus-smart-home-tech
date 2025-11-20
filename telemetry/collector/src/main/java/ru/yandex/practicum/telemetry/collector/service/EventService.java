package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

public interface EventService {

    void sendSensorEvent(SensorEvent event);

    void sendHubEvent(HubEvent event);
}