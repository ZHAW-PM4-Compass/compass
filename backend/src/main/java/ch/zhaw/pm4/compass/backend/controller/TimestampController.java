package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/timestamp")
public class TimestampController {
    @Autowired
    private TimestampService timestampService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<TimestampDto> createTimestamp(@RequestBody TimestampDto timestamp, Authentication authentication) {
        TimestampDto response = timestampService.createTimestamp(timestamp, authentication.getName());
        if(response == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/getById/{id}", produces = "application/json")
    public ResponseEntity<TimestampDto> getTimestampById(@PathVariable Long id, Authentication authentication) {
        TimestampDto response = timestampService.getTimestampById(id, authentication.getName());
        if(response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "/allbydaysheetid/{id}", produces = "application/json")
    public ResponseEntity<ArrayList<TimestampDto>> getAllTimestampByDaySheetId(@PathVariable Long id, Authentication authentication) {
        ArrayList<TimestampDto> list = timestampService.getAllTimestampsByDaySheetId(id, authentication.getName());
        if(list.size() == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(list);
    }
    @PutMapping(produces = "application/json")
    public ResponseEntity<TimestampDto> putTimestamp(@RequestBody TimestampDto timestamp, Authentication authentication) {
        TimestampDto response = timestampService.updateTimestampById(timestamp, authentication.getName());
        if(response == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteTimestamp(@PathVariable Long id, Authentication authentication) {
        timestampService.deleteTimestamp(id, authentication.getName());
    }
}