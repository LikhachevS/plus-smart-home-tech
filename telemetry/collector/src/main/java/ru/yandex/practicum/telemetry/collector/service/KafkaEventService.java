package ru.yandex.practicum.telemetry.collector.service;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventAvroMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

import java.util.Properties;

@Service
public class KafkaEventService implements EventService {

    private final Producer<String, SpecificRecordBase> producer;
    private final HubEventAvroMapper hubMapper;
    private final SensorEventAvroMapper sensorMapper;
    private final String topicHubEvent;
    private final String topicSensorEvent;

    public KafkaEventService(HubEventAvroMapper hubMapper, SensorEventAvroMapper sensorMapper) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        this.producer = new KafkaProducer<>(config);
        this.hubMapper = hubMapper;
        this.sensorMapper = sensorMapper;
        topicHubEvent = "telemetry.hubs.v1";
        topicSensorEvent = "telemetry.sensors.v1";
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