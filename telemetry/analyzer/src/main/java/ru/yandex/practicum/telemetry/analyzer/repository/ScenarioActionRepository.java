package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioAction;

import java.util.List;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, Long> {

    List<ScenarioAction> findAllByScenarioIdIn(List<Long> scenarioIds);

    void deleteBySensorId(String sensorId);
}