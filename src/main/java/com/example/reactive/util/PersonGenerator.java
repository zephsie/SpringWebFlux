package com.example.reactive.util;

import com.example.reactive.dto.PersonDTO;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PersonGenerator {

    public PersonDTO createRandomPersonDTO() {
        PersonDTO personDTO = new PersonDTO();

        personDTO.setName("Name" + new Random().nextInt());
        personDTO.setAddress("Address" + new Random().nextInt());
        personDTO.setAge(new Random().nextInt());
        personDTO.setEmail("Email" + new Random().nextInt());
        personDTO.setPhone("Phone" + new Random().nextInt());

        return personDTO;
    }
}