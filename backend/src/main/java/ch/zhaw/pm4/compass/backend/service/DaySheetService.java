package ch.zhaw.pm4.compass.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
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

	public DaySheetDto createDay(DaySheetDto createDay, String user_id) {
		DaySheet daySheet = convertDaySheetDtoToDaySheet(createDay);
		Optional<LocalUser> owner = localUserRepository.findById(user_id);
		if (owner.isEmpty())
			return null;
		daySheet.setOwner(owner.get());
		Optional<DaySheet> optional = daySheetRepository.findByDateAndOwnerId(daySheet.getDate(), user_id);
		if (optional.isPresent())
			return null;
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
	}

	public DaySheetDto getDaySheetByIdAndUserId(Long id, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByIdAndOwnerId(id, user_id);
		if (optional.isPresent())
			return convertDaySheetToDaySheetDto(optional.get());

		return null;
	}

	public DaySheetDto getDaySheetByDate(LocalDate date, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByDateAndOwnerId(date, user_id);
		if (optional.isPresent())
			return convertDaySheetToDaySheetDto(optional.get());
		return null;
	}

	public List<DaySheetDto> getAllDaySheetByUser(String userId) {
		Optional<List<DaySheet>> response = daySheetRepository.findAllByOwnerId(userId);
		return response.map(daySheets -> daySheets.stream().map(this::convertDaySheetToDaySheetDto).toList())
				.orElse(null);
	}

	public DaySheetDto updateDayNotes(UpdateDaySheetDayNotesDto updateDay, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByIdAndOwnerId(updateDay.getId(), user_id);
		if (optional.isEmpty())
			return null;
		DaySheet daySheet = optional.get();
		daySheet.setDayNotes(updateDay.getDay_notes());
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
	}

	public DaySheetDto updateConfirmed(Long day_id, String user_id) {
		Optional<DaySheet> optional = daySheetRepository.findByIdAndOwnerId(day_id, user_id);
		if (optional.isEmpty())
			return null;
		DaySheet daySheet = optional.get();
		daySheet.setConfirmed(true);
		return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
	}

	public DaySheet convertDaySheetDtoToDaySheet(DaySheetDto dayDto) {
		return new DaySheet(dayDto.getId(), dayDto.getDay_notes(), dayDto.getDate());
	}

	public DaySheetDto convertDaySheetToDaySheetDto(DaySheet daySheet) {
		List<TimestampDto> timestampDtos = new ArrayList<>();
		List<RatingDto> moodRatingDtos = new ArrayList<>();
		for (Timestamp timestamp : daySheet.getTimestamps()) {
			timestampDtos.add(timestampService.convertTimestampToTimestampDto(timestamp));
		}
		for (Rating rating : daySheet.getMoodRatings()) {
			moodRatingDtos.add(ratingService.convertEntityToDto(rating));
		}
		return new DaySheetDto(daySheet.getId(), daySheet.getDayNotes(), daySheet.getDate(), daySheet.getConfirmed(),
				timestampDtos, moodRatingDtos);
	}
}
