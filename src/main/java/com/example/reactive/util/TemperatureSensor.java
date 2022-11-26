package com.example.reactive.util;

import com.example.reactive.model.Temperature;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TemperatureSensor {
    private final ApplicationEventPublisher publisher;

    private final SecureRandom random = new SecureRandom();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public TemperatureSensor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void startProcessing() {
        this.executor.schedule(this::probe, 1, TimeUnit.SECONDS);
    }

    private void probe() {
        double temperature = 16 + random.nextGaussian() * 10;
        publisher.publishEvent(new Temperature(temperature));
        executor.schedule(this::probe, random.nextInt(5000), TimeUnit.MILLISECONDS);
        log.info("Publishing temperature reading: {}", temperature);
    }
}