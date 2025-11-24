package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "scenarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id")
    private String hubId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "scenario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ScenarioCondition> conditions;

    @OneToMany(mappedBy = "scenario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ScenarioAction> actions;
}