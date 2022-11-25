package com.example.reactive.util;

import com.example.reactive.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonDTOConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public PersonDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Person convertToEntity(com.example.reactive.dto.PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
}