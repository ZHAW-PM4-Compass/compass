package ch.zhaw.pm4.compass.backend.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;

/**
 * Service class that handles business logic related to day sheets.
 * This includes operations such as creating, retrieving, updating day sheets 
 * and integrating them with ratings, timestamps, and incidents based on 
 * user roles and other conditions,
 *
 * Using {@link DaySheetRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
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

	/**
	 * Creates a new day sheet from a DTO and assigns an owner to it based on the user ID provided.
	 * It checks for existing day sheets with the same date and owner before creating a new one.
	 *
	 * @param createDay The day sheet DTO to create.
	 * @param user_id The ID of the user who will own the new day sheet.
	 * @return The created day sheet DTO or null if a day sheet with the same date already exists.
	 */
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

	/**
	 * Retrieves a day sheet by its ID and ensures that it belongs to the specified user, 
   * unless the user is a social worker or admin.
	 *
	 * @param id The ID of the day sheet to retrieve.
	 * @param user_id The user ID used to verify ownership of the day sheet.
	 * @return The retrieved day sheet DTO or null if no suitable day sheet is found.
	 */
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

	/**
	 * Retrieves a day sheet by a specific date for a given user.
	 * This method checks if a day sheet exists for the specified date and user ID and returns it if found.
	 *
	 * @param date The date of the day sheet to retrieve.
	 * @param user_id The ID of the user who owns the day sheet.
	 * @return The day sheet DTO if found, or null if no day sheet exists for the specified date and user.
	 */
	public DaySheetDto getDaySheetByDate(LocalDate date, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByDateAndOwnerId(date, user_id);
		if (optional.isPresent())
			return convertDaySheetToDaySheetDto(optional.get(), null);
		return null;
	}

	/**
	 * Retrieves a list of all day sheets that have not been confirmed yet,
	 * filtering them by the role of their owners.
	 *
	 * @return A list of unconfirmed day sheet DTOs.
	 */
	public List<DaySheetDto> getAllDaySheetNotConfirmed() {
		List<UserDto> userDtos = userService.getAllUsers();
		List<DaySheet> daySheets = daySheetRepository.findAllByConfirmedIsFalseAndOwner_Role(UserRole.PARTICIPANT);
		return daySheets.stream().map(daySheet -> {
			UserDto user = userDtos.stream()
					.filter(userFilter -> userFilter.getUser_id().equals(daySheet.getOwner().getId())).findFirst()
					.orElse(null);
			return convertDaySheetToDaySheetDto(daySheet, user);
		}).toList();
	}

	/**
	 * Retrieves all day sheets for a specific month, incorporating user information.
	 *
	 * @param month The month and year to search within.
	 * @return A list of day sheet DTOs for the specified month.
	 */
	public List<DaySheetDto> getAllDaySheetByMonth(YearMonth month) {
		List<UserDto> userDtos = userService.getAllUsers();
		List<DaySheet> daySheets = daySheetRepository.findAllByDateBetween(month.atDay(1), month.atEndOfMonth());
		return daySheets.stream().map(daySheet -> {
			UserDto user = userDtos.stream()
					.filter(userFilter -> userFilter.getUser_id().equals(daySheet.getOwner().getId())).findFirst()
					.orElse(null);
			return convertDaySheetToDaySheetDto(daySheet, user);
		}).toList();
	}

	/**
	 * Retrieves all day sheets for a specific user and month.
	 *
	 * @param userId The ID of the user.
	 * @param month The month and year to search within.
	 * @return A list of day sheet DTOs for the specified user and month.
	 */
	public List<DaySheetDto> getAllDaySheetByUserAndMonth(String userId, YearMonth month) {
		List<DaySheet> response = daySheetRepository.findAllByOwnerIdAndDateBetween(userId, month.atDay(1),
				month.atEndOfMonth());
		return response.stream().map(daySheet -> convertDaySheetToDaySheetDto(daySheet, null))
				.collect(Collectors.toList());
	}

	/**
	 * Updates the notes for a specific day sheet.
	 *
	 * @param updateDay Contains the ID of the day sheet and the new notes to be updated.
	 * @return The updated day sheet DTO, or null if the day sheet does not exist.
	 */
	public DaySheetDto updateDayNotes(UpdateDaySheetDayNotesDto updateDay) {
		Optional<DaySheet> optional = daySheetRepository.findById(updateDay.getId());
		if (optional.isEmpty())
			return null;
		DaySheet daySheet = optional.get();
		daySheet.setDayNotes(updateDay.getDay_notes());
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet), null);
	}

	/**
	 * Updates the confirmed status of a day sheet based on user role and provided
	 * value.
	 *
	 * @param day_id The ID of the day sheet to be updated.
	 * @param value The new confirmed status to set.
	 * @param user_id The ID of the user performing the update.
	 * @return The updated day sheet DTO, or null if the day sheet does not exist or if the user lacks proper authorization.
	 */
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

	/**
	 * Converts a DaySheetDto to a DaySheet entity.
	 *
	 * @param dayDto The day sheet DTO to convert.
	 * @return A day sheet entity.
	 */
	public DaySheet convertDaySheetDtoToDaySheet(DaySheetDto dayDto) {
		return new DaySheet(dayDto.getId(), dayDto.getDay_notes(), dayDto.getDate());
	}

	/**
	 * Converts a DaySheet entity to a DaySheetDto, potentially including associated users and details.
	 *
	 * @param daySheet The day sheet entity to convert.
	 * @param owner The owner user DTO, if applicable.
	 * @return A day sheet DTO with potentially additional details included.
	 */
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
