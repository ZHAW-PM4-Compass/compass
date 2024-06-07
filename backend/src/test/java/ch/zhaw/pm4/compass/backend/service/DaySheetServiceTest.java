package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.model.*;
import ch.zhaw.pm4.compass.backend.model.dto.*;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DaySheetServiceTest {
	@Mock
	private DaySheetRepository daySheetRepository;
	@Mock
	private LocalUserRepository localUserRepository;

	@Mock
	private UserService userServiceMock;
	@Mock
	private TimestampService timestampService;
	@Mock
	private RatingService ratingService;

	@InjectMocks
	private DaySheetService daySheetService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private LocalDate dateNow = LocalDate.now();
	private String reportText = "Testdate";
	private String user_id = "löasdjflkajsdf983475908347";

	private UpdateDaySheetDayNotesDto getUpdateDaySheetDayNotesDto() {
		return new UpdateDaySheetDayNotesDto(1l, reportText + "1");
	}

	private LocalUser getLocalUser() {
		return new LocalUser(user_id, UserRole.PARTICIPANT);
	}

	private DaySheetDto getDaySheetDto() {
		return new DaySheetDto(1l, reportText, dateNow, false, new ArrayList<TimestampDto>());
	}

	private DaySheet getDaySheet() {
		return new DaySheet(1l, getLocalUser(), reportText, dateNow, false, new ArrayList<>());
	}

	@Test
	public void whenCreateDaySheet_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		DaySheetDto createDay = getDaySheetDto();
		LocalUser localUser = getLocalUser();
		when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(localUser));
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto resultDay = daySheetService.createDay(createDay, user_id);
		assertEquals(createDay.getDay_notes(), resultDay.getDay_notes());
		assertEquals(createDay.getDate(), resultDay.getDate());
	}

	@Test
	void whenGetDayById_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto foundDay = daySheetService.getDaySheetByIdAndUserId(daySheet.getId(), user_id);

		assertEquals(daySheet.getId(), foundDay.getId());
		assertEquals(daySheet.getDate(), foundDay.getDate());
		assertEquals(daySheet.getDayNotes(), foundDay.getDay_notes());
	}

	@Test
	public void whenGetDayByDate_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto foundDay = daySheetService.getDaySheetByDate(daySheet.getDate(), user_id);

		assertEquals(daySheet.getId(), foundDay.getId());
		assertEquals(daySheet.getDate(), foundDay.getDate());
		assertEquals(daySheet.getDayNotes(), foundDay.getDay_notes());
	}

	@Test
	void whenCreateExistingDaySheet_ExpectNull() {
		DaySheet daySheet = getDaySheet();
		DaySheetDto createDay = getDaySheetDto();
		LocalUser localUser = getLocalUser();
		when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(localUser));
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		assertNull(daySheetService.createDay(createDay, user_id));
	}

	@Test
	public void whenGetAllDaySheetByMonth_ExpectCorrectReturn() {
		LocalUser user = new LocalUser(user_id, UserRole.PARTICIPANT);

		DaySheet day1 = new DaySheet(1l, user, reportText, dateNow, true, new ArrayList<>());
		DaySheet day2 = new DaySheet(2l, user, reportText, dateNow.plusDays(1), true, new ArrayList<>());
		LocalDate monthFirst = dateNow.withDayOfMonth(1);
		LocalDate monthLast = dateNow.withDayOfMonth(dateNow.lengthOfMonth());

		List<DaySheet> jpaResponse = Arrays.asList(day1, day2);

		when(daySheetRepository.findAllByDateBetween(monthFirst, monthLast)).thenReturn(jpaResponse);
		List<DaySheetDto> daySheets = daySheetService.getAllDaySheetByMonth(YearMonth.from(monthFirst),user.getId());

		assertEquals(jpaResponse.size(), daySheets.size());

		for (int i = 0; i < jpaResponse.size(); i++) {
			DaySheet daySheetEntity = jpaResponse.get(i);
			DaySheetDto daySheetDto = daySheets.get(i);

			assertEquals(daySheetEntity.getId(), daySheetDto.getId());
			assertEquals(daySheetEntity.getDayNotes(), daySheetDto.getDay_notes());
			assertEquals(daySheetEntity.getDate(), daySheetDto.getDate());
			assertEquals(daySheetEntity.getConfirmed(), daySheetDto.getConfirmed());
			assertEquals(daySheetEntity.getTimestamps(), daySheetDto.getTimestamps());
		}
	}

	@Test
	public void whenGetAllDaySheetByUserAndMonth_ExpectCorrectReturn() {
		LocalUser user = new LocalUser(user_id, UserRole.PARTICIPANT);

		DaySheet day1 = new DaySheet(1l, user, reportText, dateNow, true, new ArrayList<>());
		DaySheet day2 = new DaySheet(2l, user, reportText, dateNow.plusDays(1), true, new ArrayList<>());
		LocalDate monthFirst = dateNow.withDayOfMonth(1);
		LocalDate monthLast = dateNow.withDayOfMonth(dateNow.lengthOfMonth());

		List<DaySheet> jpaResponse = Arrays.asList(day1, day2);

		when(daySheetRepository.findAllByOwnerIdAndDateBetween(user_id, monthFirst, monthLast)).thenReturn(jpaResponse);
		List<DaySheetDto> daySheets = daySheetService.getAllDaySheetByUserAndMonth(user_id, YearMonth.from(monthFirst));

		assertEquals(jpaResponse.size(), daySheets.size());

		for (int i = 0; i < jpaResponse.size(); i++) {
			DaySheet daySheetEntity = jpaResponse.get(i);
			DaySheetDto daySheetDto = daySheets.get(i);

			assertEquals(daySheetEntity.getId(), daySheetDto.getId());
			assertEquals(daySheetEntity.getDayNotes(), daySheetDto.getDay_notes());
			assertEquals(daySheetEntity.getDate(), daySheetDto.getDate());
			assertEquals(daySheetEntity.getConfirmed(), daySheetDto.getConfirmed());
			assertEquals(daySheetEntity.getTimestamps(), daySheetDto.getTimestamps());
		}
	}

	@Test
	void whenUpdateDaySheetNotes_ExpectCorrectReturn() {
		LocalUser user = getLocalUser();
		UpdateDaySheetDayNotesDto updateDay = getUpdateDaySheetDayNotesDto();
		DaySheet daySheet = getDaySheet();
		daySheet.setDayNotes(updateDay.getDay_notes());
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto getDay = daySheetService.updateDayNotes(updateDay,user.getId());
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void whenUpdateDaySheetConfirmed_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		daySheet.setConfirmed(true);
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		when(userServiceMock.getUserRole(any(String.class))).thenReturn(UserRole.SOCIAL_WORKER);
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto getDay = daySheetService.updateConfirmed(1l, true, user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
		assertEquals(daySheet.getConfirmed(), getDay.getConfirmed());
	}

	@Test
	void whenUpdateDaySheetConfirmed_ExpectNull() {
		when(daySheetRepository.findById(1l)).thenReturn(Optional.empty());
		assertNull(daySheetService.updateConfirmed(1l, true, user_id));

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(getDaySheet()));
		when(userServiceMock.getUserRole(user_id)).thenReturn(UserRole.PARTICIPANT);
		assertNull(daySheetService.updateConfirmed(1l, true, user_id));
	}

	@Test
	void whenUpdateNotExistingDaySheet_ExpectNull() {
		UpdateDaySheetDayNotesDto updateDay = getUpdateDaySheetDayNotesDto();
		DaySheet daySheet = getDaySheet();
		daySheet.setDayNotes(updateDay.getDay_notes());
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		assertNull(daySheetService.updateDayNotes(updateDay,getLocalUser().getId()));
	}

	@Test
	void whenGetDaySheetById_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto getDay = daySheetService.getDaySheetByIdAndUserId(daySheet.getId(), user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void whenGetNotExistingDaySheetById_ExpectNull() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class))).thenReturn(Optional.empty());
		assertNull(daySheetService.getDaySheetByIdAndUserId(daySheet.getId(), user_id));
	}

	@Test
	void whenGetDaySheetByDate_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto getDay = daySheetService.getDaySheetByDate(daySheet.getDate(), user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void whenGetNotExistingDaySheetByDate_ExpectCorrectReturn() {
		DaySheet daySheet = getDaySheet();
		List<DaySheet> returnlist = new ArrayList<DaySheet>();
		returnlist.add(daySheet);
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.empty());
		assertNull(daySheetService.getDaySheetByDate(daySheet.getDate(), user_id));
	}

	@Test
	void whenGetAllDaySheetNotConfirmed_ExpectCorrectReturn() {
		LocalUser user = new LocalUser(user_id, UserRole.PARTICIPANT);

		DaySheet day1 = new DaySheet(1l, user, reportText, dateNow, false, new ArrayList<>());
		DaySheet day2 = new DaySheet(2l, user, reportText, dateNow.plusDays(1), false, new ArrayList<>());

		List<DaySheet> jpaResponse = Arrays.asList(day1, day2);

		when(daySheetRepository.findAllByConfirmedIsFalseAndOwner_Role(UserRole.PARTICIPANT)).thenReturn(jpaResponse);
		List<DaySheetDto> daySheets = daySheetService.getAllDaySheetNotConfirmed(user.getId());

		assertEquals(jpaResponse.size(), daySheets.size());

		for (int i = 0; i < jpaResponse.size(); i++) {
			DaySheet daySheetEntity = jpaResponse.get(i);
			DaySheetDto daySheetDto = daySheets.get(i);

			assertEquals(daySheetEntity.getId(), daySheetDto.getId());
			assertEquals(daySheetEntity.getDayNotes(), daySheetDto.getDay_notes());
			assertEquals(daySheetEntity.getDate(), daySheetDto.getDate());
			assertEquals(daySheetEntity.getConfirmed(), daySheetDto.getConfirmed());
			assertEquals(daySheetEntity.getTimestamps(), daySheetDto.getTimestamps());
		}
	}

	@Test
	void whenFullEntityToDtoConvert_ExpectCorrectReturn() throws NotValidCategoryOwnerException {
		DaySheet daySheet = getDaySheet();
		LocalUser user = getLocalUser();
		LocalTime time1 = LocalTime.of(10, 0);
		LocalTime time2 = LocalTime.of(11, 0);
		Timestamp timestamp = new Timestamp(1l, time1, time2, daySheet);
		TimestampDto timestampDto = new TimestampDto(1l, 1l, time1, time2);

		Rating ratingOne = new Rating(3, RatingType.PARTICIPANT);
		ratingOne.setCategory(new Category("Unit Test", 0, 10, List.of()));
		ratingOne.setDaySheet(daySheet);
		CategoryDto categoryDto = new CategoryDto(1l, "Cat", 1, 10);
		RatingDto ratingDto = new RatingDto(categoryDto, new DaySheetDto(), 3, RatingType.PARTICIPANT);

		Incident incident = new Incident(1l, "Inci Titlte", "A discription", daySheet);

		daySheet.setTimestamps(List.of(timestamp));
		daySheet.setMoodRatings(List.of(ratingOne));
		daySheet.setIncidents(List.of(incident));

		when(timestampService.convertTimestampToTimestampDto(timestamp)).thenReturn(timestampDto);
		when(ratingService.convertEntityToDto(ratingOne)).thenReturn(ratingDto);

		DaySheetDto returnDto = daySheetService.convertDaySheetToDaySheetDto(daySheet, null,"");

		assertEquals(daySheet.getId(), returnDto.getId());
		assertEquals(daySheet.getDate(), returnDto.getDate());
		assertEquals(daySheet.getDayNotes(), returnDto.getDay_notes());
		assertEquals(daySheet.getConfirmed(), returnDto.getConfirmed());
		assertEquals(daySheet.getTimestamps().size(), returnDto.getTimestamps().size());
		assertEquals(daySheet.getTimestamps().getFirst().getId(), returnDto.getTimestamps().getFirst().getId());
		assertEquals(daySheet.getMoodRatings().size(), returnDto.getMoodRatings().size());
		assertEquals(daySheet.getMoodRatings().getFirst().getRating(),
				returnDto.getMoodRatings().getFirst().getRating());
		assertEquals(daySheet.getIncidents().size(), returnDto.getIncidents().size());
		assertEquals(daySheet.getIncidents().getFirst().getId(), returnDto.getIncidents().getFirst().getId());
	}
	@Test
	void whenGetDaySheetByUserAndDate_ExpectCorrectReturn()
	{

		LocalUser user = getLocalUser();
		DaySheet daySheet = getDaySheet();
		when(userServiceMock.getUserRole(any(String.class))).thenReturn(user.getRole());
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class),any(String.class))).thenReturn(Optional.of(daySheet));
		DaySheetDto returnDaySheetDto = daySheetService.getDaySheetByUserAndDate(user.getId(),daySheet.getDate(),user.getId());

		assertEquals(daySheet.getId(),returnDaySheetDto.getId());
	}
	@Test
	void whenGetDaySheetByUserAndDateSocialWorkerTriesToGetParticipantsDaySheet_ExpectCorrectReturn()
	{

		LocalUser user = getLocalUser();
		user.setRole(UserRole.SOCIAL_WORKER);
		LocalUser owner = getLocalUser();
		owner.setId("ownerId");
		DaySheet daySheet = getDaySheet();
		when(userServiceMock.getUserRole(any(String.class))).thenReturn(user.getRole());
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class),any(String.class))).thenReturn(Optional.of(daySheet));
		DaySheetDto returnDaySheetDto = daySheetService.getDaySheetByUserAndDate(owner.getId(),daySheet.getDate(),user.getId());

		assertEquals(daySheet.getId(),returnDaySheetDto.getId());
	}
	@Test
	void whenGetDaySheetByUserAndDateParticipantTriesToGetAnotherParticipantsDaySheet_ExpectNull()
	{

		LocalUser user = getLocalUser();
		LocalUser owner = getLocalUser();
		owner.setId("anotherParticipantId");
		DaySheet daySheet = getDaySheet();
		when(userServiceMock.getUserRole(any(String.class))).thenReturn(owner.getRole());
		DaySheetDto returnDaySheetDto = daySheetService.getDaySheetByUserAndDate(user.getId(),daySheet.getDate(),owner.getId());

		assertEquals(null,returnDaySheetDto);
	}
}