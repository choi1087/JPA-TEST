package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepositoryUsingDataJpa extends JpaRepository<Person, Long>{
    Person findByName(String name);

    Person findByAddress_City(String city);

    List<Person> findAll(Sort sort);

    List<Person> findAllByOrderByAgeAsc();
}
