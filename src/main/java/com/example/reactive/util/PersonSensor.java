package com.example.reactive.util;

import com.example.reactive.model.Person;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class PersonSensor {

    private final ApplicationEventPublisher publisher;

    private final SecureRandom random = new SecureRandom();

    private final PersonGenerator personGenerator;

    private final ModelMapper modelMapper;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public PersonSensor(ApplicationEventPublisher publisher, PersonGenerator personGenerator, ModelMapper modelMapper) {
        this.publisher = publisher;
        this.personGenerator = personGenerator;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void startProcessing() {
        this.executor.schedule(this::generate, 1, TimeUnit.SECONDS);
    }

    private void generate() {
        publisher.publishEvent(modelMapper.map(personGenerator.createRandomPersonDTO(), Person.class));
        executor.schedule(this::generate, random.nextInt(5000), TimeUnit.MILLISECONDS);
    }
}
