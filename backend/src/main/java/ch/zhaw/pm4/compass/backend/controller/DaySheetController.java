package ch.zhaw.pm4.compass.backend.controller;


import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/daysheet")
public class DaySheetController {

    @Autowired
    private DaySheetService daySheetService;
        @PostMapping(produces = "application/json")
        public ResponseEntity<DaySheetDto> createDaySheet(@RequestBody DaySheetDto daySheet, Authentication authentication) {
            if(authentication == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if(daySheet.getDate() == null)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            DaySheetDto response =  daySheetService.createDay(daySheet, authentication.getName());
            if(response == null)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return ResponseEntity.ok(response);
        }

        @GetMapping(path = "/getById/{id}", produces = "application/json")
        public ResponseEntity<DaySheetDto> getDaySheetById(@PathVariable Long id, Authentication authentication) {
            if(authentication == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            DaySheetDto response = daySheetService.getDaySheetById(id, authentication.getName());
            if(response == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(response);
        }

        @GetMapping(path = "/getByDate/{date}", produces = "application/json")
        public ResponseEntity<DaySheetDto> getDaySheetById(@PathVariable String date, Authentication authentication) {
            //String pattern = "yyyy-MM-dd";
            //DateFormat dateFormat = new SimpleDateFormat(pattern);
            if(authentication == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            DaySheetDto response = daySheetService.getDaySheetByDate(LocalDate.parse(date), authentication.getName());
            if(response == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(response);
        }

        @PutMapping(produces = "application/json")
        public ResponseEntity<DaySheetDto> updateDay(@RequestBody DaySheetDto updateDay, Authentication authentication)
        {
            if(authentication == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            DaySheetDto response = daySheetService.updateDay(updateDay, authentication.getName());
            if(response == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.ok(response);
        }


    }