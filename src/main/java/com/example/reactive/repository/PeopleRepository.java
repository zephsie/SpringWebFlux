package com.example.reactive.repository;

import com.example.reactive.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PeopleRepository extends ReactiveMongoRepository<Person, String> {
    Mono<Person> findByName(String name);
}