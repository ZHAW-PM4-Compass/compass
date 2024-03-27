package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.CreateTimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetDaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetTimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateTimestampDto;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timestamp")
public class TimestampController {
    @Autowired
    private TimestampService timestampService;

    @PostMapping(produces = "application/json")
    public GetTimestampDto createTimestamp(@RequestBody CreateTimestampDto timestamp) {
        return timestampService.createTimestamp(timestamp);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public GetTimestampDto getTimestampById(@PathVariable Long id) {
        return timestampService.getTimestampById(id);
    }

    @PutMapping(produces = "application/json")
    public GetTimestampDto putTimestamp(@RequestBody UpdateTimestampDto timestamp) {
        return timestampService.updateTimestampById(timestamp);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteTimestamp(@PathVariable Long id) {
        timestampService.deleteTimestamp(id);
    }
}