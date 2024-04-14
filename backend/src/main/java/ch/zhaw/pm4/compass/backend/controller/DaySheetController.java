package ch.zhaw.pm4.compass.backend.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.exception.TimestampFormatException;
import ch.zhaw.pm4.compass.backend.model.dto.CreateDaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetDaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DaySheet Controller", description = "DaySheet Endpoint")
@RestController
@RequestMapping("/daysheet")
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
    public GetDaySheetDto getDaySheetByDate(@PathVariable String date) {
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