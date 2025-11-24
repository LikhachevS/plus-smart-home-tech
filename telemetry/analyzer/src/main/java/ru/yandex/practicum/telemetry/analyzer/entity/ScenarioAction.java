package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id", insertable = false, updatable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_id", insertable = false, updatable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_id", insertable = false, updatable = false)
    private Action action;
}