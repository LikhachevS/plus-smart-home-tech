package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.telemetry.collector")
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}