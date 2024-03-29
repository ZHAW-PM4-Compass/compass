package ch.zhaw.pm4.compass.backend.controller;


import ch.zhaw.pm4.compass.backend.exception.TimestampFormatException;
import ch.zhaw.pm4.compass.backend.model.dto.*;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/daysheet")
public class DaySheetController {
    @Autowired
    private DaySheetService daySheetService;


    @PostMapping(produces = "application/json")
    public GetDaySheetDto createDaySheet(@RequestBody CreateDaySheetDto daySheet) {
        return daySheetService.createDay(daySheet);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public GetDaySheetDto getDaySheetById(@PathVariable Long id) {
        return daySheetService.getDaySheetById(id);
    }

    @GetMapping(path = "/getByDate/{date}", produces = "application/json")
    public GetDaySheetDto getDaySheetById(@PathVariable String date) {
        String pattern = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return daySheetService.getDaySheetByDate(dateFormat.parse(date));
        } catch (ParseException e) {
            throw new TimestampFormatException();
        }
    }

    @PutMapping(produces = "application/json")
    public GetDaySheetDto updateDay(@RequestBody UpdateDaySheetDto updateDay)
    {
        return daySheetService.updateDay(updateDay);
    }
}