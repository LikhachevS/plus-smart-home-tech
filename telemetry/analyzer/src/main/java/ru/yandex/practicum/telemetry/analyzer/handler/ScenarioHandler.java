package ru.yandex.practicum.telemetry.analyzer.handler;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.client.HubRouterClient;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioHandler {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterClient hubRouterClient;

    public void checkScenarios(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.info("Начинаем проверку сценариев для хаба: {}", hubId);

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.debug("Для хаба {} не найдено активных сценариев", hubId);
        } else {
            for (Scenario scenario : scenarios) {
                if (isScenarioConditionsMet(scenario, snapshot)) {
                    log.info("Сценарий '{}' (ID: {}) выполнен для хаба {}",
                            scenario.getName(), scenario.getId(), hubId);

                    sendScenarioActions(scenario, hubId);
                }
            }
        }
    }

    private void sendScenarioActions(Scenario scenario, String hubId) {
        List<ScenarioAction> scenarioActions = scenario.getActions();

        for (ScenarioAction scenarioAction : scenarioActions) {
            try {

                DeviceActionProto deviceAction = DeviceActionProto.newBuilder()
                        .setSensorId(scenarioAction.getSensor().getId()) // ID датчика/устройства
                        .setType(ActionTypeProto.valueOf(scenarioAction.getAction().getType()))
                        .setValue(scenarioAction.getAction().getValue()) // преобразуем value в строку
                        .build();

                DeviceActionRequest request = DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenario.getName())
                        .setAction(deviceAction)
                        .setTimestamp(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .setNanos(Instant.now().getNano())
                                .build())
                        .build();

                hubRouterClient.sendDeviceActions(request);

                log.info("Отправлено действие для устройства {} (тип: {}) по сценарию '{}' (ID: {})",
                        scenarioAction.getSensor().getId(),
                        scenarioAction.getAction().getType(),
                        scenario.getName(),
                        scenario.getId());

            } catch (Exception e) {
                log.error("Ошибка при отправке действия для сценария '{}' (ID: {}, ScenarioAction ID: {}): {}",
                        scenario.getName(),
                        scenario.getId(),
                        scenarioAction.getId(),
                        e.getMessage(), e);
            }
        }
    }
    private boolean isScenarioConditionsMet(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<ScenarioCondition> conditions = scenario.getConditions();

        Set<String> snapshotSensorIds = snapshot.getSensorsState().keySet();

        for (ScenarioCondition sc : conditions) {
            String sensorId = sc.getSensor().getId();

            if (!snapshotSensorIds.contains(sensorId)) {
                log.debug("Датчик {} отсутствует в снимке — сценарий не выполняется", sensorId);
                return false;
            }

            SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
            Condition condition = sc.getCondition();

            if (!checkCondition(condition, sensorState)) {
                return false;
            }
        }
        return true;

    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorState) {
        try {
            ConditionTypeAvro conditionType = ConditionTypeAvro.valueOf(condition.getType());
            ConditionOperationAvro operation = ConditionOperationAvro.valueOf(condition.getOperation());
            Integer conditionValue = condition.getValue();

            // Извлекаем значение датчика в зависимости от типа
            switch (conditionType) {
                case MOTION:
                    MotionSensorAvro motionSensor = (MotionSensorAvro) sensorState.getData();
                    int motionVal = motionSensor.getMotion() ? 1 : 0;
                    return compareByOperator(motionVal, conditionValue, operation);

                case LUMINOSITY:
                    LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
                    int lightVal = lightSensor.getLuminosity();
                    return compareByOperator(lightVal, conditionValue, operation);


                case SWITCH:
                    SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
                    int switchVal = switchSensor.getState() ? 1 : 0;
                    return compareByOperator(switchVal, conditionValue, operation);

                case TEMPERATURE:
                    if (sensorState.getData() instanceof TemperatureSensorAvro) {
                        TemperatureSensorAvro tempSensor = (TemperatureSensorAvro) sensorState.getData();
                        int tempVal = tempSensor.getTemperatureC();
                        return compareByOperator(tempVal, conditionValue, operation);
                    }
                    else if (sensorState.getData() instanceof ClimateSensorAvro) {
                        ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                        int tempVal = climateSensor.getTemperatureC();  // Извлекаем температуру из ClimateSensor
                        return compareByOperator(tempVal, conditionValue, operation);
                    }
                    else {
                        log.error("Некорректный тип датчика для условия TEMPERATURE. Ожидался TemperatureSensorAvro или ClimateSensorAvro, но получен: {}",
                                sensorState.getData().getClass().getName());
                        return false;
                    }

                case CO2LEVEL:
                    ClimateSensorAvro co2Sensor = (ClimateSensorAvro) sensorState.getData();
                    int co2Value = co2Sensor.getCo2Level();
                    return compareByOperator(co2Value, conditionValue, operation);

                case HUMIDITY:
                    ClimateSensorAvro humiditySensor = (ClimateSensorAvro) sensorState.getData();
                    int humidityValue = humiditySensor.getHumidity();
                    return compareByOperator(humidityValue, conditionValue, operation);

                default:
                    log.warn("Неподдерживаемый тип условия: {}", conditionType);
                    return false;
            }
        } catch (ClassCastException e) {
            log.error("Ошибка приведения типа датчика для условия типа {}: {}",
                    condition.getType(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке условия {}: {}",
                    condition.getType(), e.getMessage());
            return false;
        }
    }

    private boolean compareByOperator(Integer sensorValue, Integer conditionValue, ConditionOperationAvro operation) {
        return switch (operation) {
            case EQUALS -> sensorValue.equals(conditionValue);
            case GREATER_THAN -> sensorValue > conditionValue;
            case LOWER_THAN -> sensorValue < conditionValue;
        };
    }
}
