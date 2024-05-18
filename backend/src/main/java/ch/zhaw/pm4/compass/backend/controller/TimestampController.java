package ch.zhaw.pm4.compass.backend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Timestamp Controller", description = "Timestamp Endpoint")
@RestController
@RequestMapping("/timestamp")
public class TimestampController {
	@Autowired
	private TimestampService timestampService;
	@Autowired
	private DaySheetService daySheetService;

	@PostMapping(produces = "application/json")
	public ResponseEntity<TimestampDto> createTimestamp(@RequestBody TimestampDto timestamp,
			Authentication authentication) {
		if (!timestamp.verifyTimeStamp()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		TimestampDto response = timestampService.createTimestamp(timestamp, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/getById/{id}", produces = "application/json")
	public ResponseEntity<TimestampDto> getTimestampById(@PathVariable Long id, Authentication authentication) {
		TimestampDto response = timestampService.getTimestampById(id, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/allbydaysheetid/{id}", produces = "application/json")
	public ResponseEntity<ArrayList<TimestampDto>> getAllTimestampByDaySheetId(@PathVariable Long id,
			Authentication authentication) {
		ArrayList<TimestampDto> list = timestampService.getAllTimestampsByDaySheetId(id, authentication.getName());
		if (list.size() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(list);
	}

	@PutMapping(produces = "application/json")
	public ResponseEntity<TimestampDto> putTimestamp(@RequestBody TimestampDto timestamp,
			Authentication authentication) {
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		TimestampDto response = timestampService.updateTimestampById(timestamp, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Object> deleteTimestamp(@PathVariable Long id, Authentication authentication) {
		TimestampDto timestamp = timestampService.getTimestampById(id, authentication.getName());
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		timestampService.deleteTimestamp(id, authentication.getName());
		return ResponseEntity.ok().build();
	}
}