package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioCondition {

    @NotBlank(message = "Идентификатор датчика не может быть пустым")
    private String sensorId;

    @NotBlank(message = "Тип условия не может быть пустым")
    private ConditionType type;

    @NotBlank(message = "Операция не может быть пустой")
    private ConditionOperation operation;

    private Integer value;
}