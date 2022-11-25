package com.example.reactive.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PersonDTO {

    private String name;

    private String email;

    private String phone;

    private String address;

    private Integer age;
}