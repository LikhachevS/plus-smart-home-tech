package ru.yandex.practicum.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.service.EventService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final EventService eventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEventProto = event.getScenarioAdded();

        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEventProto.getConditionList()
                .stream()
                .map(this::mapCondition)
                .collect(Collectors.toList());

        List<DeviceActionAvro> deviceActionAvroList = scenarioAddedEventProto.getActionList()
                .stream()
                .map(this::mapAction)
                .collect(Collectors.toList());

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .setConditions(scenarioConditionAvroList)
                .setActions(deviceActionAvroList)
                .build();

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(scenarioAddedEventAvro)
                .build();

        eventService.sendHubEvent(hubEventAvro);
    }

    private DeviceActionAvro mapAction(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private ActionTypeAvro mapActionType(ActionTypeProto type) {
        return ActionTypeAvro.valueOf(type.name());
    }

    private ScenarioConditionAvro mapCondition(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapConditionType(condition.getType()))
                .setOperation(mapOperation(condition.getOperation()))
                .setValue(getValueScenarioCondition(condition))
                .build();
    }

    private ConditionTypeAvro mapConditionType(ConditionTypeProto type) {
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro mapOperation(ConditionOperationProto op) {
        return ConditionOperationAvro.valueOf(op.name());
    }

    private Object getValueScenarioCondition(ScenarioConditionProto scenarioConditionProto) {
        if (scenarioConditionProto.hasBoolValue()) {
            return scenarioConditionProto.getBoolValue();
        } else if (scenarioConditionProto.hasIntValue()) {
            return scenarioConditionProto.getIntValue();
        } else {
            return null;
        }
    }
}
