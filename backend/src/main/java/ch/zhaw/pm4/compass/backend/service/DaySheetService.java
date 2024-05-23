package ch.zhaw.pm4.compass.backend.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.zhaw.pm4.compass.backend.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;

@Service
public class DaySheetService {
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private LocalUserRepository localUserRepository;

	@Autowired
	TimestampService timestampService;
	@Autowired
	RatingService ratingService;

	@Autowired
	UserService userService;

	public DaySheetDto createDay(DaySheetDto createDay, String user_id) {
		DaySheet daySheet = convertDaySheetDtoToDaySheet(createDay);
		Optional<LocalUser> owner = localUserRepository.findById(user_id);
		if (owner.isEmpty())
			return null;
		daySheet.setOwner(owner.get());
		Optional<DaySheet> optional = daySheetRepository.findByDateAndOwnerId(daySheet.getDate(), user_id);
		if (optional.isPresent())
			return null;
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet), null);
	}

	public DaySheetDto getDaySheetByIdAndUserId(Long id, String user_id) {
		Optional<DaySheet> optional;
		UserRole userRole = userService.getUserRole(user_id);

		if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN) {
			optional = daySheetRepository.findById(id);
		} else {
			optional = daySheetRepository.findByIdAndOwnerId(id, user_id);
		}

		if (optional.isPresent()) {
			UserDto owner = userService.getUserById(user_id);
			return convertDaySheetToDaySheetDto(optional.get(), owner);
		}

		return null;
	}

	public DaySheetDto getDaySheetByDate(LocalDate date, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByDateAndOwnerId(date, user_id);
		if (optional.isPresent())
			return convertDaySheetToDaySheetDto(optional.get(), null);
		return null;
	}

	public List<DaySheetDto> getAllDaySheetNotConfirmed() {
		List<UserDto> userDtos = userService.getAllUsers();
		List<DaySheet> daySheets = daySheetRepository.findAllByConfirmedIsFalseAndOwner_Role(UserRole.PARTICIPANT);
		return daySheets
				.stream().map(daySheet -> {
					UserDto user = userDtos.stream()
							.filter(userFilter -> userFilter.getUser_id().equals(daySheet.getOwner().getId()))
							.findFirst().orElse(null);
					return convertDaySheetToDaySheetDto(daySheet, user);
				}).toList();
	}

	public List<DaySheetDto> getAllDaySheetByMonth(YearMonth month) {
		List<UserDto> userDtos = userService.getAllUsers();
		List<DaySheet> daySheets = daySheetRepository.findAllByDateBetween(month.atDay(1), month.atEndOfMonth());
		return daySheets
				.stream().map(daySheet -> {
					UserDto user = userDtos.stream()
							.filter(userFilter -> userFilter.getUser_id().equals(daySheet.getOwner().getId()))
							.findFirst().orElse(null);
					return convertDaySheetToDaySheetDto(daySheet, user);
				}).toList();
	}

	public List<DaySheetDto> getAllDaySheetByUserAndMonth(String userId, YearMonth month) {
		List<DaySheet> response = daySheetRepository.findAllByOwnerIdAndDateBetween(userId, month.atDay(1),
				month.atEndOfMonth());
		return response.stream().map(daySheet -> convertDaySheetToDaySheetDto(daySheet, null)).collect(Collectors.toList());
	}

	public DaySheetDto updateDayNotes(UpdateDaySheetDayNotesDto updateDay) {
		Optional<DaySheet> optional = daySheetRepository.findById(updateDay.getId());
		if (optional.isEmpty())
			return null;
		DaySheet daySheet = optional.get();
		daySheet.setDayNotes(updateDay.getDay_notes());
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet), null);
	}

	public DaySheetDto updateConfirmed(Long day_id, boolean value, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findById(day_id);
		if (optional.isEmpty())
			return null;
		UserRole userRole = userService.getUserRole(user_id);
		if (userRole != UserRole.SOCIAL_WORKER && userRole != UserRole.ADMIN) {
			return null;
		}
		DaySheet daySheet = optional.get();
		daySheet.setConfirmed(value);
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet), null);
	}

	public DaySheet convertDaySheetDtoToDaySheet(DaySheetDto dayDto) {
		return new DaySheet(dayDto.getId(), dayDto.getDay_notes(), dayDto.getDate());
	}

	public DaySheetDto convertDaySheetToDaySheetDto(DaySheet daySheet, UserDto owner) {
		List<TimestampDto> timestampDtos = new ArrayList<>();
		List<RatingDto> moodRatingDtos = new ArrayList<>();
		List<IncidentDto> incidentDtos = new ArrayList<>();
		for (Timestamp timestamp : daySheet.getTimestamps()) {
			timestampDtos.add(timestampService.convertTimestampToTimestampDto(timestamp));
		}
		for (Rating rating : daySheet.getMoodRatings()) {
			moodRatingDtos.add(ratingService.convertEntityToDto(rating));
		}

		for (Incident incident : daySheet.getIncidents()) {
			IncidentDto incidentDto = IncidentDto.builder().id(incident.getId()).title(incident.getTitle())
					.description(incident.getDescription()).date(incident.getDaySheet().getDate()).build();
			incidentDtos.add(incidentDto);
		}
		return new DaySheetDto(daySheet.getId(), daySheet.getDayNotes(), daySheet.getDate(), daySheet.getConfirmed(),
				timestampDtos, moodRatingDtos, incidentDtos, owner);
	}
}
