package ch.zhaw.pm4.compass.backend.controller;

import java.util.List;

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

import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.IncidentNotFoundException;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.service.IncidentService;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Incident Controller", description = "Incident Enpoint")
@RestController
@RequestMapping("/incident")
public class IncidentController {
	@Autowired
	private IncidentService incidentService;

	@PostMapping(produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<IncidentDto> createIncident(@RequestBody IncidentDto incident) {
		try {
			return ResponseEntity.ok(incidentService.createIncident(incident));
		} catch (DaySheetNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<IncidentDto> updateIncident(@RequestBody IncidentDto incident) {
		try {
			return ResponseEntity.ok(incidentService.updateIncident(incident));
		} catch (IncidentNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteIncident(@PathVariable Long id) {
		try {
			incidentService.deleteIncident(id);
			return ResponseEntity.ok(id);
		} catch (IncidentNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/getAll", produces = "application/json")
	public ResponseEntity<List<IncidentDto>> getAllIncidents(Authentication authentication) {
		return ResponseEntity.ok(incidentService.getAll());
	}
}
