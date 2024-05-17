package ch.zhaw.pm4.compass.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DaySheet Controller", description = "DaySheet Endpoint")
@RestController
@RequestMapping("/daysheet")
public class DaySheetController {
	@Autowired
	private DaySheetService daySheetService;

	@PostMapping(produces = "application/json")
	public ResponseEntity<DaySheetDto> createDaySheet(@RequestBody DaySheetDto daySheet,
			Authentication authentication) {
		if (daySheet.getDate() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		DaySheetDto response = daySheetService.createDay(daySheet, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/getById/{id}", produces = "application/json")
	public ResponseEntity<DaySheetDto> getDaySheetById(@PathVariable Long id, Authentication authentication) {
		DaySheetDto response = daySheetService.getDaySheetByIdAndUserId(id, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/getByDate/{date}", produces = "application/json")
	public ResponseEntity<DaySheetDto> getDaySheetDate(@PathVariable String date, Authentication authentication) {
		DaySheetDto response = daySheetService.getDaySheetByDate(LocalDate.parse(date), authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/getAllByParticipant/{userId}", produces = "application/json")
	public ResponseEntity<List<DaySheetDto>> getAllDaySheetByParticipant(@PathVariable String userId,
			Authentication authentication) {
		return ResponseEntity.ok(daySheetService.getAllDaySheetByUser(userId));
	}

	@PutMapping(path = "/updateDayNotes", produces = "application/json")
	public ResponseEntity<DaySheetDto> updateDayNotes(@RequestBody UpdateDaySheetDayNotesDto updateDay,
			Authentication authentication) {
		DaySheetDto response = daySheetService.updateDayNotes(updateDay, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}

	@PutMapping(path = "/confirm/{id}", produces = "application/json")
	public ResponseEntity<DaySheetDto> updateConfirmed(@PathVariable Long id, Authentication authentication) {
		if (authentication == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		DaySheetDto response = daySheetService.updateConfirmed(id, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}
}