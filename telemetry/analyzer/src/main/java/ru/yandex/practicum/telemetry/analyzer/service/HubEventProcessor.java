package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfig;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final KafkaConfig kafkaConfig;
    private final HubEventHandler eventHandler;

    @Override
    public void run() {

        try {
            String topicHubEvent = kafkaConfig.getTopicHubEvent();

            consumer.subscribe(List.of(topicHubEvent));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    Object payload = event.getPayload();

                    switch (payload) {
                        case DeviceAddedEventAvro deviceAdded -> {
                            eventHandler.saveDevice(deviceAdded.getId(), event.getHubId());
                            log.info("Сенсор {} успешно добавлен в хаб {}!", deviceAdded.getId(), event.getHubId());
                        }
                        case ScenarioAddedEventAvro scenarioAdded -> {
                            eventHandler.saveScenario(event, scenarioAdded);
                            log.info("Сценарий {} успешно добавлен!", scenarioAdded.getName());
                        }
                        case DeviceRemovedEventAvro deviceRemoved -> {
                            eventHandler.removeDevice(deviceRemoved.getId(), event.getHubId());
                            log.info("Сенсор {} успешно удалён!", deviceRemoved.getId());
                        }
                        case ScenarioRemovedEventAvro scenarioRemoved -> {
                            eventHandler.removeScenario(scenarioRemoved.getName(), event.getHubId());
                            log.info("Сценарий {} успешно удалён!", scenarioRemoved.getName());
                        }
                        default -> log.warn("Неизвестный тип события: {}", payload.getClass().getSimpleName());
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
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
            }
        }
    }
}