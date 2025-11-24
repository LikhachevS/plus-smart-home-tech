package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;

import java.util.List;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, Long> {

    List<ScenarioCondition> findAllByScenarioIdIn(List<Long> scenarioIds);

    void deleteBySensorId(String sensorId);
}