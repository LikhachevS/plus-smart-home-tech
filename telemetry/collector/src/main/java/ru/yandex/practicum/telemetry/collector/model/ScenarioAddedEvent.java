package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NotNull
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank(message = "Название сценария не может быть пустым")
    @Size(min = 3, message = "Название сценария должно содержать не менее 3 символов")
    private String name;

    @NotNull(message = "Список условий не может быть пустым")
    private List<ScenarioCondition> conditions;

    @NotNull(message = "Список действий не может быть пустым")
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}