package com.example.reactive.controller;

import com.example.reactive.model.Person;
import com.example.reactive.repository.PeopleRepository;
import com.example.reactive.util.PersonDTOConverter;
import com.example.reactive.util.PersonGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
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
    public Flux<Person> index() {
        return peopleRepository.findAll();
    }

    @GetMapping(value = "/people/delayed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Person> indexWithDelay() {
        return peopleRepository.findAll().delayElements(Duration.ofSeconds(1));
    }

    @GetMapping("/new/random")
    public Mono<Person> newRandom() {
        return peopleRepository.save(personDTOConverter.convertToEntity(personGenerator.createRandomPersonDTO()));
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

    @GetMapping("/new/random/many")
    public Flux<Person> newRandomMany() {
        return Flux.range(1, 10)
                .map(i -> personGenerator.createRandomPersonDTO())
                .map(personDTOConverter::convertToEntity)
                .flatMap(peopleRepository::save);
    }

    @GetMapping("/{name}")
    public Mono<ResponseEntity<Person>> getPersonByName(@PathVariable String name) {
        return peopleRepository.findByName(name)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/remove/all")
    public Mono<Void> removeAll() {
        return peopleRepository.deleteAll();
    }
}