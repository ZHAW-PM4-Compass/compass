package ch.zhaw.pm4.compass.backend.controller;


import ch.zhaw.pm4.compass.backend.model.dto.*;
import ch.zhaw.pm4.compass.backend.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/day")
public class DayController {
    @Autowired
    private DayService dayService;


    @PostMapping(produces = "application/json")
    public GetDayDto createDay(@RequestBody CreateDayDto day) {
        return dayService.createDay(day);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public GetDayDto getDayById(@PathVariable Long id) {
        return dayService.getDayById(id);
    }

    @GetMapping(path = "/getByDate/{date}", produces = "application/json")
    public GetDayDto getDayById(@PathVariable Date date) {
        return dayService.getDayByDate(date);
    }

    @PutMapping(produces = "application/json")
    public GetDayDto updateDay(@RequestBody UpdateDayDto updateDay)
    {
        return dayService.updateDay(updateDay);
    }
}