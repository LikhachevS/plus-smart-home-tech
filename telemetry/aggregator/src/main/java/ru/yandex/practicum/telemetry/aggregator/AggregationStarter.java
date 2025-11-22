package ru.yandex.practicum.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.aggregator.service.SensorsSnapshotService;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SensorsSnapshotService snapshotService;
    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaConfig kafkaConfig;

    public void start() {
        try {
            String sensorsTopic = kafkaConfig.getTopicSensorEvent();
            String snapshotsTopic = kafkaConfig.getTopicSnapshot();

            consumer.subscribe(List.of(sensorsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();

                    Optional<SensorsSnapshotAvro> snapshotOpt = snapshotService.updateState(event);

                    if (snapshotOpt.isPresent()) {
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(snapshotsTopic,
                                null, event.getTimestamp().toEpochMilli(), event.getHubId(), snapshotOpt.get());

                        producer.send(message);
                    }
                }
                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("Получено исключение WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}