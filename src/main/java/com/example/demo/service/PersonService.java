package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.Person;
import com.example.demo.repository.PersonRepositoryUsingDataJpa;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepositoryUsingDataJpa personRepositoryUsingDataJpa;

    public void save(String name, int age, String city, String street) {

        Address address = Address.builder()
                .city(city)
                .street(street)
                .build();

        Person person = Person.builder()
                .name(name)
                .address(address)
                .age(age)
                .build();

        personRepositoryUsingDataJpa.save(person);
    }
}
