package com.example.reactive.controller;

import com.example.reactive.model.Person;
import com.example.reactive.repository.PeopleRepository;
import com.example.reactive.util.PersonDTOConverter;
import com.example.reactive.util.PersonGenerator;
import com.example.reactive.view.PersonView;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@Slf4j
@RequestMapping(produces = "application/json")
public class PeopleController {

    private final PeopleRepository peopleRepository;

    private final PersonDTOConverter personDTOConverter;

    private final PersonGenerator personGenerator;

    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @Autowired
    public PeopleController(PeopleRepository peopleRepository, PersonDTOConverter personDTOConverter, PersonGenerator personGenerator) {
        this.peopleRepository = peopleRepository;
        this.personDTOConverter = personDTOConverter;
        this.personGenerator = personGenerator;
    }

    @GetMapping("/people")
    @JsonView(PersonView.Min.class)
    public Flux<Person> getAllPeople() {
        return peopleRepository.findAll();
    }

    @GetMapping("/people/{id}")
    @JsonView(PersonView.Full.class)
    public Mono<ResponseEntity<Person>> show(@PathVariable String id) {
        return peopleRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .timeout(Duration.ofSeconds(0))
                .onErrorResume(throwable -> {
                    log.error("Error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping(value = "/people/delayed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @JsonView(PersonView.Min.class)
    public Flux<Person> indexWithDelay() {
        return peopleRepository.findAll().delayElements(Duration.ofMillis(100));
    }

    @GetMapping("/new/random")
    public Mono<ResponseEntity<Person>> createRandom() {
        return peopleRepository.save(personDTOConverter.convertToEntity(personGenerator.createRandomPersonDTO()))
                .map(ResponseEntity::ok)
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(throwable -> {
                    log.error("Error: {}", throwable.getMessage());
                    return Mono.empty();
                });
    }

    @GetMapping("/people-stream")
    public SseEmitter events(HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);

        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onCompletion(() -> clients.remove(emitter));
        return emitter;
    }

    @Async
    @EventListener
    public void handleMessage(Person person) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        clients.forEach(emitter -> {
            try {
                emitter.send(person, MediaType.APPLICATION_JSON);
            } catch (Exception ignore) {
                deadEmitters.add(emitter);
            }
        });

        deadEmitters.forEach(clients::remove);
    }

    @GetMapping("/{name}")
    public Mono<ResponseEntity<Person>> getPersonByName(@PathVariable String name) {
        return peopleRepository.findByName(name)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(throwable -> {
                    log.error("Error: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}