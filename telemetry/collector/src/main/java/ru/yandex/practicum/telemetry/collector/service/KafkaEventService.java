package ru.yandex.practicum.telemetry.collector.service;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventAvroMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

@Service
public class KafkaEventService implements EventService {

    private final Producer<String, SpecificRecordBase> producer;
    private final HubEventAvroMapper hubMapper;
    private final SensorEventAvroMapper sensorMapper;
    private final String topicHubEvent = "telemetry.hubs.v1";
    private final String topicSensorEvent = "telemetry.sensors.v1";

    public KafkaEventService(KafkaProducer<String, SpecificRecordBase> producer,
                             HubEventAvroMapper hubMapper,
                             SensorEventAvroMapper sensorMapper) {
        this.producer = producer;
        this.hubMapper = hubMapper;
        this.sensorMapper = sensorMapper;
    }

    @Override
    public void sendSensorEvent(SensorEvent event) {
        SpecificRecordBase eventAvro = sensorMapper.toAvro(event);
        String hubId = event.getHubId();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicSensorEvent, hubId, eventAvro);
        producer.send(record);
    }

    @Override
    public void sendHubEvent(HubEvent event) {
        SpecificRecordBase eventAvro = hubMapper.toAvro(event);
        String hubId = event.getHubId();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicHubEvent, hubId, eventAvro);
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