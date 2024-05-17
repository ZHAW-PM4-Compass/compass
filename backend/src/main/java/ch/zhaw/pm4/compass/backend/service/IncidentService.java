package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {
	@Autowired
	private IncidentRepository incidentRepository;
	@Autowired
	private DaySheetService daySheetService;
	@Autowired
	private UserService userService;

	public IncidentDto createIncident(IncidentDto createIncident) throws DaySheetNotFoundException {
		DaySheetDto daySheetDto = daySheetService.getDaySheetByDate(createIncident.getDate(), createIncident.getUser().getUser_id());
		if (daySheetDto == null) {
			DaySheetDto createDaySheetDto = new DaySheetDto("", createIncident.getDate(), false);
			daySheetDto = daySheetService.createDay(createDaySheetDto, createIncident.getUser().getUser_id());
		}

		DaySheet daySheet = daySheetService.convertDaySheetDtoToDaySheet(daySheetDto);

		Incident incident = convertDtoToEntity(createIncident);
		incident.setDaySheet(daySheet);

		return convertEntityToDto(incidentRepository.save(incident), null);
	}

	public IncidentDto updateIncident(IncidentDto updateIncident) throws IncidentNotFoundException {
		Incident incident = incidentRepository.findById(updateIncident.getId())
				.orElseThrow(() -> new IncidentNotFoundException(updateIncident.getId()));
		incident.setTitle(updateIncident.getTitle());
		incident.setDescription(updateIncident.getDescription());
		return convertEntityToDto(incidentRepository.save(incident),null);
	}

	public void deleteIncident(Long id) throws IncidentNotFoundException {
		Incident incident = incidentRepository.findById(id)
				.orElseThrow(() -> new IncidentNotFoundException(id));
		incidentRepository.delete(incident);
	}

	public List<IncidentDto> getAll(String userId) {
		UserDto userDto = userService.getUserById(userId);
		if (userDto.getRole().equals(UserRole.SOCIAL_WORKER) || userDto.getRole().equals(UserRole.ADMIN)) {
			List<UserDto> userDtos = userService.getAllUsers();
			return incidentRepository.findAll()
					.stream().map(incident -> {
						UserDto user = userDtos.stream()
								.filter(userFilter -> userFilter.getUser_id().equals(incident.getDaySheet().getOwner().getId()))
								.findFirst().orElse(null);
						return convertEntityToDto(incident, user);
					}).toList();
		} else {
			return incidentRepository.findAllByDaySheet_Owner_Id(userId)
					.stream().map(incident -> convertEntityToDto(incident, null)).toList();
		}
	}

	public Incident convertDtoToEntity(IncidentDto dto) {
		return new Incident(dto.getTitle(), dto.getDescription());
	}

	public IncidentDto convertEntityToDto(Incident entity, UserDto userDto) {
		return IncidentDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.description(entity.getDescription())
				.date(entity.getDaySheet().getDate())
				.user(userDto)
				.build();
	}
}
