package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
public class Address {

    private String city;

    private String street;

    @Builder
    public Address(String city, String street){
        this.city = city;
        this.street = street;
    }
}
