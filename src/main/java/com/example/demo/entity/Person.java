package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @Column(name = "PER_ID")
    private Long id;

    @Column(name = "PER_NAME")
    private String name;

    @Column(name = "PER_AGE")
    private Integer age;

    @Embedded
    private Address address;

    @Builder
    public Person(Long id, String name, Address address, int age){
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
