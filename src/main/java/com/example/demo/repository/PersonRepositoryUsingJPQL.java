package com.example.demo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import com.example.demo.entity.Person;

import org.springframework.stereotype.Repository;

@Repository
public class PersonRepositoryUsingJPQL {

    private final EntityManager em;

    PersonRepositoryUsingJPQL(EntityManager em) {
        this.em = em;
    }

    public void flush() {
        em.flush();
    }

    public void save(Person person) {
        em.persist(person);
    }

    public Person findOne(Long id) {
        return em.find(Person.class, id);
    }

    public List<Person> findAll() {
        return em.createQuery("select p from Person p", Person.class)
                .getResultList();
    }

    public Person findByName(String name) {
        return (Person) em.createQuery("select p from Person p where p.name=:name")
                .setParameter("name", name)
                .getSingleResult();
    }
}
