package com.example.reactive.model;

import com.example.reactive.view.PersonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "person")
@Data
@NoArgsConstructor
public class Person {

    @Id
    @JsonView(PersonView.Min.class)
    private String id;

    @JsonView(PersonView.Min.class)
    private String name;

    @JsonView(PersonView.Min.class)
    private String email;

    @JsonView(PersonView.Full.class)
    private String phone;

    @JsonView(PersonView.Full.class)
    private String address;

    @JsonView(PersonView.Full.class)
    private Integer age;
}