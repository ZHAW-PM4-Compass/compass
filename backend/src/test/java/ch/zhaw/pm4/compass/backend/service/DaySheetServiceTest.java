package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;

class DaySheetServiceTest {

	@Mock
	private DaySheetRepository daySheetRepository;

	@InjectMocks
	private DaySheetService daySheetService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private LocalDate dateNow = LocalDate.now();
	private String reportText = "Testdate";
	private String user_id = "löasdjflkajsdf983475908347";

	private DaySheetDto getUpdateDaySheet() {
		return new DaySheetDto(1l, reportText + "1", dateNow.plusDays(1), true);
	}

	private DaySheetDto getDaySheetDto() {
		return new DaySheetDto(1l, reportText, dateNow, false, new ArrayList<TimestampDto>());
	}

	private DaySheet getDaySheet() {
		return new DaySheet(1l, user_id, reportText, dateNow, false, new ArrayList<>());
	}

	@Test
	public void testCreateDaySheet() {
		DaySheet daySheet = getDaySheet();
		DaySheetDto createDay = getDaySheetDto();
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto resultDay = daySheetService.createDay(createDay, user_id);
		assertEquals(createDay.getDay_notes(), resultDay.getDay_notes());
		assertEquals(createDay.getDate(), resultDay.getDate());
	}

	@Test
	void testGetDayById() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto foundDay = daySheetService.getDaySheetById(daySheet.getId(), user_id);

		assertEquals(daySheet.getId(), foundDay.getId());
		assertEquals(daySheet.getDate(), foundDay.getDate());
		assertEquals(daySheet.getDayNotes(), foundDay.getDay_notes());
	}

	@Test
	public void testGetDayByDate() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByDateAndUserId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto foundDay = daySheetService.getDaySheetByDate(daySheet.getDate(), user_id);

		assertEquals(daySheet.getId(), foundDay.getId());
		assertEquals(daySheet.getDate(), foundDay.getDate());
		assertEquals(daySheet.getDayNotes(), foundDay.getDay_notes());
	}

	@Test
	void testCreateExistingDaySheet() {
		DaySheet daySheet = getDaySheet();
		DaySheetDto createDay = getDaySheetDto();
		when(daySheetRepository.findByDateAndUserId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		assertNull(daySheetService.createDay(createDay, user_id));
	}

	@Test
	void testUpdateDaySheetNotes() {
		DaySheetDto updateDay = getUpdateDaySheet();
		DaySheet daySheet = getDaySheet();
		daySheet.setDayNotes(updateDay.getDay_notes());
		when(daySheetRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto getDay = daySheetService.updateDayNotes(updateDay, user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void testUpdateDaySheetConfirmed() {
		DaySheetDto updateDay = getUpdateDaySheet();
		DaySheet daySheet = getDaySheet();
		// daySheet.setDayNotes(updateDay.getDay_notes());
		daySheet.setConfirmed(updateDay.getConfirmed());
		when(daySheetRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		DaySheetDto getDay = daySheetService.updateDayNotes(updateDay, user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
		assertEquals(daySheet.getConfirmed(), getDay.getConfirmed());
	}

	@Test
	void testUpdateNotExistingDaySheet() {
		DaySheetDto updateDay = getUpdateDaySheet();
		DaySheet daySheet = getDaySheet();
		daySheet.setDayNotes(updateDay.getDay_notes());
		when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);
		assertNull(daySheetService.updateDayNotes(updateDay, user_id));
	}

	@Test
	void testGetDaySheetById() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndUserId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto getDay = daySheetService.getDaySheetById(daySheet.getId(), user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void testGetNotExistingDaySheetById() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByIdAndUserId(any(Long.class), any(String.class))).thenReturn(Optional.empty());
		assertNull(daySheetService.getDaySheetById(daySheet.getId(), user_id));
	}

	@Test
	void testGetDaySheetByDate() {
		DaySheet daySheet = getDaySheet();
		when(daySheetRepository.findByDateAndUserId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));
		DaySheetDto getDay = daySheetService.getDaySheetByDate(daySheet.getDate(), user_id);
		assertEquals(daySheet.getId(), getDay.getId());
		assertEquals(daySheet.getDate(), getDay.getDate());
		assertEquals(daySheet.getDayNotes(), getDay.getDay_notes());
	}

	@Test
	void testGetNotExistingDaySheetByDate() {
		DaySheet daySheet = getDaySheet();
		List<DaySheet> returnlist = new ArrayList<DaySheet>();
		returnlist.add(daySheet);
		when(daySheetRepository.findByDateAndUserId(any(LocalDate.class), any(String.class)))
				.thenReturn(Optional.empty());
		assertNull(daySheetService.getDaySheetByDate(daySheet.getDate(), user_id));
	}
}