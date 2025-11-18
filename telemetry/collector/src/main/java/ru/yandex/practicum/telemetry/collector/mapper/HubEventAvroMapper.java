package ru.yandex.practicum.telemetry.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventAvroMapper {

    public HubEventAvro toAvro(HubEvent event) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp());

        switch (event.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent deviceEvent = (DeviceAddedEvent) event;

                // Создаём Avro-объект для DeviceAddedEvent
                DeviceAddedEventAvro deviceAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceEvent.getId())
                        .setType(mapDeviceType(deviceEvent.getDeviceType()))
                        .build();

                // Просто кладём объект в payload — Avro сам определит тип union
                builder.setPayload(deviceAvro);
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceEvent = (DeviceRemovedEvent) event;

                DeviceRemovedEventAvro deviceAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceEvent.getId())
                        .build();

                builder.setPayload(deviceAvro);
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioEvent = (ScenarioAddedEvent) event;

                List<ScenarioConditionAvro> conditionsAvro = scenarioEvent.getConditions()
                        .stream()
                        .map(this::mapCondition)
                        .collect(Collectors.toList());

                List<DeviceActionAvro> actionsAvro = scenarioEvent.getActions()
                        .stream()
                        .map(this::mapAction)
                        .collect(Collectors.toList());

                ScenarioAddedEventAvro scenarioAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioEvent.getName())
                        .setConditions(conditionsAvro)
                        .setActions(actionsAvro)
                        .build();

                builder.setPayload(scenarioAvro);
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioEvent = (ScenarioRemovedEvent) event;

                ScenarioRemovedEventAvro scenarioAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioEvent.getName())
                        .build();

                builder.setPayload(scenarioAvro);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported HubEventType: " + event.getType());
        }

        return builder.build();
    }

    private DeviceActionAvro mapAction(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private ActionTypeAvro mapActionType(ActionType type) {
        return ActionTypeAvro.valueOf(type.name());
    }

    private ScenarioConditionAvro mapCondition(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapConditionType(condition.getType()))
                .setOperation(mapOperation(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    private ConditionTypeAvro mapConditionType(ConditionType type) {
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro mapOperation(ConditionOperation op) {
        return ConditionOperationAvro.valueOf(op.name());
    }

    private DeviceTypeAvro mapDeviceType(DeviceType type) {
        return DeviceTypeAvro.valueOf(type.name());
    }
}