package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.Day;
import ch.zhaw.pm4.compass.backend.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/day")
public class DayController {
    @Autowired
    private DayService dayService;

    @GetMapping()
    public String index() {
        return "Hallo von Compass Spring Boot!";
    }

    @PostMapping(produces = "application/json")
    public Day createDay(@RequestBody Day day) {

        return dayService.createDay(day);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Day getDayById(@PathVariable Long id) {
        return dayService.getDayById(id);
    }
}