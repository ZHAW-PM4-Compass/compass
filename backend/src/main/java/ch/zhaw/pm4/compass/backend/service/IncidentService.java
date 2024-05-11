package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {
	@Autowired
	private IncidentRepository incidentRepository;
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private UserService userService;

	public IncidentDto createIncident(IncidentDto createIncident) throws DaySheetNotFoundException {
		long daySheetId = createIncident.getDaySheet().getId();
		DaySheet daysheet = daySheetRepository.findById(daySheetId)
				.orElseThrow(() -> new DaySheetNotFoundException(daySheetId));

		Incident incident = convertDtoToEntity(createIncident);
		incident.setDaySheet(daysheet);

		return convertEntityToDto(incidentRepository.save(incident));
	}

	public IncidentDto updateIncident(IncidentDto updateIncident) throws IncidentNotFoundException {
		Incident incident = incidentRepository.findById(updateIncident.getId())
				.orElseThrow(() -> new IncidentNotFoundException(updateIncident.getId()));
		incident.setTitle(updateIncident.getTitle());
		incident.setDescription(updateIncident.getDescription());
		return convertEntityToDto(incidentRepository.save(incident));
	}

	public void deleteIncident(Long id) throws IncidentNotFoundException {
		Incident incident = incidentRepository.findById(id)
				.orElseThrow(() -> new IncidentNotFoundException(id));
		incidentRepository.delete(incident);
	}

	public List<IncidentDto> getAll() {
		return incidentRepository.findAll()
				.stream().map(this::convertEntityToDto).toList();
	}

	public List<IncidentDto> getAllIncidentByUser(String userId) {
		return incidentRepository.findAllByDaySheetUserId(userId)
				.stream().map(this::convertEntityToDto).toList();
	}

	Incident convertDtoToEntity(IncidentDto dto) {
		return new Incident(dto.getTitle(), dto.getDescription());
	}

	IncidentDto convertEntityToDto(Incident entity) {
		return IncidentDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.description(entity.getDescription())
				.build();
	}
}
