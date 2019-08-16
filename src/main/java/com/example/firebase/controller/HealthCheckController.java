package com.example.firebase.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
public class HealthCheckController {


    @GetMapping("/")
        public String hello() {
                Date now = new Date();
                String output = "<a href=./swagger-ui.html> swagger docs <a> <br>"+
                "Hello Spring Boot!!!"+now.toString();
                return output;
        }

}