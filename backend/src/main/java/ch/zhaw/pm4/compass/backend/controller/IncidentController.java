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

/**
 * Controller for managing incidents within the Compass application.
 * Provides RESTful endpoints for creating, updating, and deleting incidents, as well as retrieving all incidents.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "Incident Controller", description = "Incident Enpoint")
@RestController
@RequestMapping("/incident")
public class IncidentController {
	@Autowired
	private IncidentService incidentService;

	/**
	 * Creates a new incident with the provided incident details.
	 *
	 * @param incident Incident data transfer object containing the details of the incident.
	 * @return ResponseEntity with the created IncidentDto or BAD_REQUEST if the incident could not be created.
	 * @throws DaySheetNotFoundException if the related day sheet is not found.
	 */
	@PostMapping(produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<IncidentDto> createIncident(@RequestBody IncidentDto incident) {
		try {
			return ResponseEntity.ok(incidentService.createIncident(incident));
		} catch (DaySheetNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing incident based on the provided incident DTO.
	 *
	 * @param incident Incident data transfer object containing the updated details of the incident.
	 * @return ResponseEntity with the updated IncidentDto or BAD_REQUEST if the incident could not be found or updated.
	 * @throws IncidentNotFoundException if the incident to be updated is not found.
	 */
	@PutMapping(produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<IncidentDto> updateIncident(@RequestBody IncidentDto incident) {
		try {
			return ResponseEntity.ok(incidentService.updateIncident(incident));
		} catch (IncidentNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Deletes an incident by its ID.
	 *
	 * @param id The unique identifier of the incident to be deleted.
	 * @return ResponseEntity with the ID of the deleted incident or BAD_REQUEST if the incident could not be found or deleted.
	 * @throws IncidentNotFoundException if the incident to be deleted is not found.
	 */
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteIncident(@PathVariable Long id) {
		try {
			incidentService.deleteIncident(id);
			return ResponseEntity.ok(id);
		} catch (IncidentNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a list of all incidents.
	 *
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity containing a list of all IncidentDto.
	 */
	@GetMapping(path = "/getAll", produces = "application/json")
	public ResponseEntity<List<IncidentDto>> getAllIncidents(Authentication authentication) {
		return ResponseEntity.ok(incidentService.getAll());
	}
}
