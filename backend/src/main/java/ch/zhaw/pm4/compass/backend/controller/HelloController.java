package ch.zhaw.pm4.compass.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public String index() {
        return "Hallo von Compass Spring Boot!";
    }

}
