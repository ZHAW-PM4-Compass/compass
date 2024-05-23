package ch.zhaw.pm4.compass.backend.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;

@Service
public class TimestampService {
	@Autowired
	private TimestampRepository timestampRepository;
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private UserService userService;

	public TimestampDto createTimestamp(TimestampDto createTimestamp, String user_id) {
		Timestamp timestamp = convertTimestampDtoToDTimestamp(createTimestamp, user_id);
		if (timestamp != null && checkNoDoubleEntry(timestamp)) {
			return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
		} else {
			return null;
		}
	}

	public TimestampDto getTimestampById(Long id, String userId) {
		if (!authCheckTimestamp(id, userId)) {
			return null;
		}

		Optional<Timestamp> response = timestampRepository.findById(id);
		System.out.println(response.get());
		if (response.isEmpty())
			return null;

		return convertTimestampToTimestampDto(response.get());
	}

	public ArrayList<TimestampDto> getAllTimestampsByDaySheetId(Long id, String userId) {
		if (!authCheckDaySheet(id, userId)) {
			return null;
		}

		ArrayList<TimestampDto> resultList = new ArrayList<>();
		Iterable<Timestamp> list = timestampRepository.findAllByDaySheetId(id);
		for (Timestamp timestamp : list)
			resultList.add(convertTimestampToTimestampDto(timestamp));

		return resultList;
	}

	public TimestampDto updateTimestampById(TimestampDto updateTimestampDto, String userId) {
		if (!authCheckTimestamp(updateTimestampDto.getId(), userId)) {
			return null;
		}

		Optional<Timestamp> response = timestampRepository.findById(updateTimestampDto.getId());
		if (response.isPresent()) {
			Timestamp timestamp = response.get();
			Timestamp newTimestamp = new Timestamp(timestamp.getId(), updateTimestampDto.getStart_time(),
					updateTimestampDto.getEnd_time(), timestamp.getDaySheet());
			if (checkNoDoubleEntry(newTimestamp)) {
				timestamp.setStartTime(updateTimestampDto.getStart_time());
				timestamp.setEndTime(updateTimestampDto.getEnd_time());
				return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
			}
		}

		return null;

	}

	private boolean authCheckTimestamp(Long timestampId, String userId) {
		Optional<Timestamp> optionalTimestamp = timestampRepository.findById(timestampId);
		UserRole userRole = userService.getUserRole(userId);
		if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN || (optionalTimestamp.isPresent()
				&& optionalTimestamp.get().getDaySheet().getOwner().getId().equals(userId))) {
			return true;
		}

		return false;
	}

	private boolean authCheckDaySheet(Long daySheetId, String userId) {
		if (userService.getUserRole(userId) == UserRole.SOCIAL_WORKER || userService.getUserRole(userId) == UserRole.ADMIN
				|| daySheetRepository.findById(daySheetId).get().getOwner().getId().equals(userId)) {
			return true;
		}
		return false;
	}

	public void deleteTimestamp(Long id) {
		Optional<Timestamp> timestamp = timestampRepository.findById(id);
        timestamp.ifPresent(value -> timestampRepository.delete(value));
	}

	private Timestamp convertTimestampDtoToDTimestamp(TimestampDto timestampDto, String user_id) {
		Optional<DaySheet> daySheet;
		UserRole userRole = userService.getUserRole(user_id);

		if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN) {
			daySheet = daySheetRepository.findById(timestampDto.getDay_sheet_id());
		} else {
			daySheet = daySheetRepository.findByIdAndOwnerId(timestampDto.getDay_sheet_id(), user_id);
		}

		if (daySheet.isPresent()) {
			return new Timestamp(timestampDto.getId(), timestampDto.getStart_time(), timestampDto.getEnd_time(), daySheet.get());
		}
		return null;
	}

	public TimestampDto convertTimestampToTimestampDto(Timestamp timestamp) {
		return new TimestampDto(timestamp.getId(), timestamp.getDaySheet().getId(), timestamp.getStartTime(),
				timestamp.getEndTime());
	}

	public boolean checkNoDoubleEntry(Timestamp timestampToCheck) {
		Iterable<Timestamp> timestamps = timestampRepository
				.findAllByDaySheetId(timestampToCheck.getDaySheet().getId());

		boolean noDoubleEntry = true;
		if (timestampToCheck.getStartTime().isAfter(timestampToCheck.getEndTime())
				|| timestampToCheck.getStartTime().equals(timestampToCheck.getEndTime())) {
			noDoubleEntry = false;
			return noDoubleEntry;
		}
		for (Timestamp timestamp : timestamps) {
			if (timestampToCheck.getId() == timestamp.getId()) {
				continue;
			}
			if (timestampToCheck.getStartTime().isBefore(timestamp.getEndTime())
					&& timestampToCheck.getStartTime().isAfter(timestamp.getStartTime())) {
				noDoubleEntry = false;
				break;
			}

			if (timestampToCheck.getEndTime().isBefore(timestamp.getEndTime())
					&& timestampToCheck.getEndTime().isAfter(timestamp.getStartTime())) {
				noDoubleEntry = false;
				break;
			}

			if (timestampToCheck.getStartTime().equals(timestamp.getStartTime())
					|| timestampToCheck.getEndTime().equals(timestamp.getEndTime())) {
				noDoubleEntry = false;
				break;
			}
			if (timestampToCheck.getStartTime().isBefore(timestamp.getStartTime())
					&& timestampToCheck.getEndTime().isAfter(timestamp.getEndTime())) {
				noDoubleEntry = false;
				break;
			}
		}
		return noDoubleEntry;
	}
}
