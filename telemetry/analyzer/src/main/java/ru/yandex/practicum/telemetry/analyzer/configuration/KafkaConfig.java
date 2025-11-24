package ru.yandex.practicum.telemetry.analyzer.configuration;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.deserializer.HubEventDeserializer;
import ru.yandex.practicum.kafka.deserializer.SensorsSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    private String bootstrapServers = "localhost:9092";
    private Boolean enableAutoCommit = false;

    private String hubEventClientId = "Consumer1";
    private String hubEventGroupId = "HubEventConsumer";
    private Class<?> hubEventKeyDeserializer = StringDeserializer.class;
    private Class<?> hubEventValueDeserializer = HubEventDeserializer.class;

    private String snapshotClientId = "Consumer2";
    private String snapshotGroupId = "SnapshotConsumer";
    private Class<?> snapshotKeyDeserializer = StringDeserializer.class;
    private Class<?> snapshotValueDeserializer = SensorsSnapshotDeserializer.class;

    @Getter
    private final String topicHubEvent = "telemetry.hubs.v1";
    @Getter
    private String topicSnapshot = "telemetry.snapshots.v1";

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, hubEventClientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubEventGroupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubEventKeyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventValueDeserializer);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        return new KafkaConsumer<>(config);
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> sensorsSnapshotConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotClientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotKeyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        return new KafkaConsumer<>(config);
    }
}