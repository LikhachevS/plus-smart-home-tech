package ru.yandex.practicum.telemetry.collector.service;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Service
public class KafkaEventService implements EventService {

    private final Producer<String, SpecificRecordBase> producer;
    private final String topicHubEvent = "telemetry.hubs.v1";
    private final String topicSensorEvent = "telemetry.sensors.v1";

    public KafkaEventService(KafkaProducer<String, SpecificRecordBase> producer) {
        this.producer = producer;
    }

    @Override
    public void sendSensorEvent(SensorEventAvro eventAvro) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicSensorEvent, null,
                Instant.now().toEpochMilli(), eventAvro.getHubId(), eventAvro);
        producer.send(record);
    }

    @Override
    public void sendHubEvent(HubEventAvro eventAvro) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicHubEvent, null,
                Instant.now().toEpochMilli(), eventAvro.getHubId(), eventAvro);
        producer.send(record);
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}