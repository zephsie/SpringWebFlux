package com.example.reactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "person")
@Data
@NoArgsConstructor
public class Person {

    @Id
    @JsonIgnore
    private String id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private Integer age;
}