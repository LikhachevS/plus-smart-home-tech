package ru.yandex.practicum.telemetry.analyzer.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventHandler {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;


    @Transactional
    public void saveDevice(String sensorId, String hubId) {
        if (sensorRepository.existsById(sensorId)) {
            log.info("Сенсор {} уже существует в системе", sensorId);
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build();
        sensorRepository.save(sensor);

        log.info("Сенсор {} успешно сохранён для хаба {}", sensorId, hubId);
    }

    @Transactional
    public void removeDevice(String sensorId, String hubId) {
        Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Сенсор " + sensorId + " не найден"));
        sensorRepository.delete(sensor);

        log.info("Сенсор {} успешно удалён для хаба {}", sensorId, hubId);
    }

    @Transactional
    public void saveScenario(HubEventAvro event, ScenarioAddedEventAvro added) {

        Scenario newScenario = Scenario.builder()
                .hubId(event.getHubId())
                .name(added.getName())
                .build();

        Scenario savedScenario = scenarioRepository.save(newScenario);

        added.getConditions().forEach(condition -> {
            Integer value = null;
            if (condition.getValue() instanceof Boolean boolVal) {
                value = boolVal ? 1 : 0;
            } else if (condition.getValue() instanceof Integer intVal) {
                value = intVal;
            }

            Condition newCondition = Condition.builder()
                    .type(condition.getType().toString())
                    .operation(condition.getOperation().toString())
                    .value(value)
                    .build();

            Condition savedCondition = conditionRepository.save(newCondition);

            Sensor sensor = sensorRepository.findByIdAndHubId(condition.getSensorId(), event.getHubId())
                    .orElseThrow(() -> new EntityNotFoundException("Сенсор " + condition.getSensorId() + " не найден"));

            ScenarioConditionId sensorId = ScenarioConditionId.builder()
                    .scenarioId(savedScenario.getId())
                    .sensorId(sensor.getId())
                    .conditionId(savedCondition.getId())
                    .build();

            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(sensorId)
                    .scenario(savedScenario)
                    .sensor(sensor)
                    .condition(savedCondition)
                    .build();

            scenarioConditionRepository.save(scenarioCondition);
        });

        added.getActions().forEach(action -> {
            Action newAction = Action.builder()
                    .type(action.getType().toString())
                    .value(action.getValue())
                    .build();

            Action saveAction = actionRepository.save(newAction);

            Sensor sensor = sensorRepository.findByIdAndHubId(action.getSensorId(), event.getHubId())
                    .orElseThrow(() -> new EntityNotFoundException("Сенсор " + action.getSensorId() + " не найден"));

            ScenarioActionId actionId = ScenarioActionId.builder()
                    .scenarioId(savedScenario.getId())
                    .sensorId(sensor.getId())
                    .actionId(saveAction.getId())
                    .build();

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(actionId)
                    .scenario(savedScenario)
                    .sensor(sensor)
                    .action(saveAction)
                    .build();

            scenarioActionRepository.save(scenarioAction);
        });
    }

    public void removeScenario (String name, String hubId) {

    }
}