package com.example.demo.controller;

import com.example.demo.service.PersonService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonService personService;

    @GetMapping("/api/save")
    public String save(
    ){
        personService.save("kim", 20, "seoul", "streetName");
        System.out.println("성공");
        return "성공";
    }
}
