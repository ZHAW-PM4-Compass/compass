package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;

class TimestampServiceTest {

	@Mock
	private TimestampRepository timestampRepository;

	@InjectMocks
	private TimestampService timestampService;

	@Mock
	private DaySheetRepository daySheetRepository;

	@Mock
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		MockitoAnnotations.openMocks(daySheetRepository);
		timestamp = getTimestamp();
		timestampDto = getTimestampDto();
		daySheet = getDaySheet();
	}

	private String reportText = "Testdate";

	private String user_id = "k234öljk43öj4öj";
	private Timestamp timestamp;
	private TimestampDto timestampDto;
	private DaySheet daySheet;

	LocalUser getLocalUser() {
		return new LocalUser(user_id, UserRole.PARTICIPANT);
	}

	DaySheet getDaySheet() {
		return new DaySheet(1l, getLocalUser(), reportText, LocalDate.now(), false, new ArrayList<>());
	}

	private TimestampDto getTimestampDto() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"));
	}

	private TimestampDto getUpdateTimestamp() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("15:00:00"));
	}

	private Timestamp getTimestamp() {
		return new Timestamp(1l, getDaySheet(), LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"));
	}

	@Test
	public void testCreateTimestamp() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		// when(daySheetRepository.getDaySheetById(any(Long.class))).thenReturn(Optional.of(daySheet));
		TimestampDto resultTimestamp = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(timestampDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	public void testCreateTimestampOfNotExistingDaySheet() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class))).thenReturn(Optional.empty());

		TimestampDto resultTimestamp = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(null, resultTimestamp);
	}

	@Test
	void testGetTimestampById() {
		timestamp.setUserId(user_id);
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		TimestampDto resultTimestamp = timestampService.getTimestampById(timestampDto.getId(), user_id);
		assertEquals(timestampDto.getId(), resultTimestamp.getId());
		assertEquals(timestampDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	public void testGetNotExistingTimestampById() {
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.empty());
		TimestampDto resultTimestamp = timestampService.getTimestampById(timestampDto.getId(), user_id);
		assertNull(resultTimestamp);
	}

	@Test
	void testCreateExistingTimestamp() {
		timestamp.setUserId(user_id);
		List<Timestamp> timestamps = new ArrayList<>();
		timestamps.add(timestamp);

		when(timestampRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(timestamp));

		when(timestampRepository.findAllByDaySheetIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(timestamps);
		TimestampDto result = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp1() {
		timestamp.setUserId(user_id);
		List<Timestamp> timestamps = new ArrayList<>();
		timestamps.add(timestamp);
		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("12:00:00"));
		when(timestampRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(timestamps);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp2() {
		timestamp.setUserId(user_id);
		List<Timestamp> timestamps = new ArrayList<>();
		timestamps.add(timestamp);
		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("13:30:00"));
		when(timestampRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(timestamps);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp3() {
		timestamp.setUserId(user_id);
		List<Timestamp> timestamps = new ArrayList<>();
		timestamps.add(timestamp);
		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("12:30:00"));
		timestampUpdateDto.setEnd_time(LocalTime.parse("13:30:00"));
		when(timestampRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(timestamps);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testAllTimestampsByDayId() {
		TimestampDto getTimestampDto0 = getTimestampDto();
		TimestampDto getTimestampDto1 = getUpdateTimestamp();
		getTimestampDto1.setId(2l);
		getTimestampDto1.setStart_time(LocalTime.parse("14:00:00"));
		getTimestampDto1.setEnd_time(LocalTime.parse("15:00:00"));
		daySheet.setOwner(getLocalUser());
		Timestamp timestamp1 = new Timestamp(1l, daySheet, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"));
		timestamp1.setUserId(user_id);
		daySheet.getTimestamps().add(timestamp1);
		Timestamp timestamp2 = new Timestamp(2l, daySheet, LocalTime.parse("14:00:00"), LocalTime.parse("15:00:00"));
		timestamp2.setUserId(user_id);
		daySheet.getTimestamps().add(timestamp2);
		ArrayList<TimestampDto> timestampsDto = new ArrayList<TimestampDto>();
		timestampsDto.add(getTimestampDto0);
		timestampsDto.add(getTimestampDto1);
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(daySheet.getTimestamps());
		// when(daySheetRepository.findByIdAndUserId(any(Long.class),
		// any(String.class))).thenReturn(Optional.of(daySheet));
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		ArrayList<TimestampDto> res = timestampService.getAllTimestampsByDaySheetId(daySheet.getId(), user_id);
		TimestampDto resTimestampDto0 = res.get(0);
		TimestampDto resTimestampDto1 = res.get(1);

		assertEquals(getTimestampDto0.getId(), resTimestampDto0.getId());
		assertEquals(getTimestampDto0.getDay_sheet_id(), resTimestampDto0.getDay_sheet_id());
		assertEquals(getTimestampDto0.getStart_time().toString(), resTimestampDto0.getStart_time().toString());
		assertEquals(getTimestampDto0.getEnd_time().toString(), resTimestampDto0.getEnd_time().toString());

		assertEquals(getTimestampDto1.getId(), resTimestampDto1.getId());
		assertEquals(getTimestampDto1.getDay_sheet_id(), resTimestampDto1.getDay_sheet_id());
		assertEquals(getTimestampDto1.getStart_time().toString(), resTimestampDto1.getStart_time().toString());
		assertEquals(getTimestampDto1.getEnd_time().toString(), resTimestampDto1.getEnd_time().toString());
		verify(timestampRepository, times(1)).findAllByDaySheetId(any(Long.class));
	}

	@Test
	void testUpdateTimestamp() {
		timestamp.setUserId(user_id);
		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		TimestampDto resultTimestamp = timestampService.updateTimestampById(timestampUpdateDto, user_id);
		assertEquals(timestampUpdateDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampUpdateDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampUpdateDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	void testUpdateNotExistingTimestamp() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.empty());
		TimestampDto resultTimestamp = timestampService.updateTimestampById(timestampDto, user_id);
		assertEquals(null, resultTimestamp);
	}

}