package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timestamp")
public class TimestampController {
    @Autowired
    private TimestampService timestampService;

    @PostMapping(path = "/{day_id}",produces = "application/json")
    public Timestamp createTimestamp(@RequestBody Timestamp timestamp,@PathVariable Long day_id) {
        return timestampService.createTimestamp(timestamp,day_id);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Timestamp getTimestampById(@PathVariable Long id) {
        return timestampService.getTimestampById(id);
    }

    @PutMapping(produces = "application/json")
    public Timestamp putTimestamp(@RequestBody Timestamp timestamp) {
        return timestampService.updateTimestampById(timestamp);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteTimestamp(@PathVariable Long id) {
        timestampService.deleteTimestamp(id);
    }
}