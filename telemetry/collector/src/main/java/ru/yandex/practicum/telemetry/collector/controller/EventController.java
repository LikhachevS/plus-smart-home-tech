package ru.yandex.practicum.telemetry.collector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.EventService;

@RestController
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/sensors")
    public void collectSensorEvents(@RequestBody SensorEvent request) {

        eventService.sendSensorEvent(request);
    }

    @PostMapping("/hubs")
    public void collectHubEvents(@RequestBody HubEvent request) {

        eventService.sendHubEvent(request);
    }
}