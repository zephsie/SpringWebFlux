package com.example.reactive.controller;

import com.example.reactive.model.Person;
import com.example.reactive.repository.PeopleRepository;
import com.example.reactive.util.PersonDTOConverter;
import com.example.reactive.util.PersonGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(produces = "application/json")
public class PeopleController {

    private final PeopleRepository peopleRepository;

    private final PersonDTOConverter personDTOConverter;

    private final PersonGenerator personGenerator;

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

    @GetMapping("/new/random")
    public Mono<Person> newRandom() {
        return peopleRepository.save(personDTOConverter.convertToEntity(personGenerator.createRandomPersonDTO()));
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