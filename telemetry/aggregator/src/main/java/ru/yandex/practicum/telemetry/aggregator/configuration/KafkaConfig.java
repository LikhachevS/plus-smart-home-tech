package ru.yandex.practicum.telemetry.aggregator.configuration;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    private String bootstrapServers = "localhost:9092";
    private Class<?> keySerializer = StringSerializer.class;
    private Class<?> valueSerializer = GeneralAvroSerializer.class;

    private String clientId = "Consumer1";
    private String groupId = "AggregatorConsumer";
    private Class<?> keyDeserializer = StringDeserializer.class;
    private Class<?> valueDeserializer = SensorEventDeserializer.class;
    private Boolean enableAutoCommit = false;

    @Getter
    private String topicSensorEvent = "telemetry.sensors.v1";
    @Getter
    private String topicSnapshot = "telemetry.snapshots.v1";

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return new KafkaProducer<>(config);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        return new KafkaConsumer<>(config);
    }
}