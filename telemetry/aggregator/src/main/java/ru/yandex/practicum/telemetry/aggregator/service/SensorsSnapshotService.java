package ru.yandex.practicum.telemetry.aggregator.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SensorsSnapshotService {
    Map<String, SensorsSnapshotAvro> snapshots;

    public SensorsSnapshotService() {
        this.snapshots = new HashMap<>();
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (snapshots.containsKey(event.getHubId())) {
            Optional<SensorsSnapshotAvro> updatedSnapshotOpt = updateSnapshot(event);
            updatedSnapshotOpt.ifPresent(sensorsSnapshotAvro -> snapshots.put(event.getHubId(), sensorsSnapshotAvro));
            return updatedSnapshotOpt;
        } else {
            Map<String, SensorStateAvro> sensorStates = new HashMap<>();

            SensorStateAvro sensorState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            sensorStates.put(event.getId(), sensorState);

            SensorsSnapshotAvro newSnapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorStates)
                    .build();

            snapshots.put(event.getHubId(), newSnapshot);

            return Optional.of(newSnapshot);
        }
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        SensorsSnapshotAvro oldSnapshot = snapshots.get(event.getHubId());
        if (oldSnapshot.getSensorsState().containsKey(event.getId())) {
            if (oldSnapshot.getSensorsState().get(event.getId()).getTimestamp().isAfter(event.getTimestamp()) ||
                    oldSnapshot.getSensorsState().get(event.getId()).getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro sensorState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        oldSnapshot.getSensorsState().put(event.getId(), sensorState);
        oldSnapshot.setTimestamp(event.getTimestamp());

        return Optional.of(oldSnapshot);
    }
}