package com.example.SpringBootDemoApplication.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String query) {   //Now, you have to use ?keyword=... instead of ?query=...
        return "Searching for: " + query;
    }
}