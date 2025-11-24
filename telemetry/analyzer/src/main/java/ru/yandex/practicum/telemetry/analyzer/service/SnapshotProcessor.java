package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.analyzer.handler.ScenarioHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final KafkaConfig kafkaConfig;
    private final ScenarioHandler scenarioHandler;

    @Override
    public void run() {

        try {
            String topicSnapshot = kafkaConfig.getTopicSnapshot();

            consumer.subscribe(List.of(topicSnapshot));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    scenarioHandler.checkScenarios(snapshot);
                }
                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("Получено исключение WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
            }
        }
    }
}