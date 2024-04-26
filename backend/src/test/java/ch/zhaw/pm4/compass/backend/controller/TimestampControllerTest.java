package ch.zhaw.pm4.compass.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class TimestampControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;
	@MockBean
	private TimestampService timestampService;
	@MockBean
	private TimestampRepository timestampRepository;
	@MockBean
	private DaySheetService daySheetService;

	@MockBean
	private DaySheetRepository daySheetRepository;
	@MockBean
	@SuppressWarnings("unused")
	private JwtDecoder jwtDecoder;
	static String token = "";
	@SuppressWarnings("unused")
	private LocalDate dateNow = LocalDate.now();
	private String reportText = "Testdate";

	DaySheet getDaySheet() {
		return new DaySheet(1l, reportText, LocalDate.now(), false);
	}

	private TimestampDto getTimestampDto() {
		return new TimestampDto(1l, 1l, Time.valueOf("13:00:00"), Time.valueOf("14:00:00"));
	}

	private TimestampDto getUpdateTimestamp() {
		return new TimestampDto(1l, 1l, Time.valueOf("13:00:00"), Time.valueOf("15:00:00"));
	}

	@SuppressWarnings("unused")
	private Timestamp getTimestamp() {
		return new Timestamp(1l, getDaySheet(), Time.valueOf("13:00:00"), Time.valueOf("14:00:00"));
	}

	@Before(value = "")
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateTimestamp() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(getTimestamp);

		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"start_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(getTimestamp.getStart_time().toString()))
				.andExpect(jsonPath("$.end_time").value(getTimestamp.getEnd_time().toString()));

		verify(timestampService, times(1)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void testTimestampAlreadyExists() throws Exception {
		// Arrange
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(null);

		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"start_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden())
				.andExpect(jsonPath("$").doesNotExist());

		verify(timestampService, times(1)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testUpdateTimestamp() throws Exception {
		// Arrange
		getUpdateTimestamp();
		TimestampDto updateTimestamp = getUpdateTimestamp();
		when(timestampService.updateTimestampById(any(TimestampDto.class), any(String.class)))
				.thenReturn(updateTimestamp);

		// Act and Assert
		mockMvc.perform(put("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + updateTimestamp.getStart_time().toString()
						+ "\", \"start_time\": \"" + updateTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(updateTimestamp.getStart_time().toString()))
				.andExpect(jsonPath("$.end_time").value(updateTimestamp.getEnd_time().toString()));

		verify(timestampService, times(1)).updateTimestampById(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetTimestampById() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.getTimestampById(any(Long.class), any(String.class))).thenReturn(getTimestamp);

		// Act and Assert//
		mockMvc.perform(
				get("/timestamp/getById/" + getTimestamp.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(getTimestamp.getStart_time().toString()))
				.andExpect(jsonPath("$.end_time").value(getTimestamp.getEnd_time().toString()));

		verify(timestampService, times(1)).getTimestampById(any(Long.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetAllTimestampsByDayId() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		TimestampDto getTimestamp1 = getUpdateTimestamp();
		getUpdateTimestamp().setId(2l);
		getTimestamp1.setStart_time(Time.valueOf("14:00:00"));
		getTimestamp1.setEnd_time(Time.valueOf("15:00:00"));
		DaySheet daySheet = getDaySheet();
		daySheet.getTimestamps().add(new Timestamp(1l, daySheet, Time.valueOf("13:00:00"), Time.valueOf("14:00:00")));
		daySheet.getTimestamps().add(new Timestamp(2l, daySheet, Time.valueOf("14:00:00"), Time.valueOf("15:00:00")));
		ArrayList<TimestampDto> timestamps = new ArrayList<TimestampDto>();
		timestamps.add(getTimestamp);
		timestamps.add(getTimestamp1);
		when(timestampService.getAllTimestampsByDaySheetId(any(Long.class), any(String.class))).thenReturn(timestamps);

		// Act and Assert//
		String res = mockMvc
				.perform(get("/timestamp/allbydaysheetid/" + daySheet.getId())
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(
				"[{\"id\":1,\"day_sheet_id\":1,\"start_time\":\"13:00:00\",\"end_time\":\"14:00:00\"},{\"id\":1,\"day_sheet_id\":1,\"start_time\":\"14:00:00\",\"end_time\":\"15:00:00\"}]",
				res);
		verify(timestampService, times(1)).getAllTimestampsByDaySheetId(any(Long.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetAllTimestampsByDayIdEmpty() throws Exception {
		// Arrange
		DaySheet daySheet = getDaySheet();
		ArrayList<TimestampDto> timestamps = new ArrayList<TimestampDto>();
		when(timestampService.getAllTimestampsByDaySheetId(any(Long.class), any(String.class))).thenReturn(timestamps);

		// Act and Assert//.header("Authorization", "Bearer " + token))
		mockMvc.perform(
				get("/timestamp/allbydaysheetid/" + daySheet.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isNotFound());
		verify(timestampService, times(1)).getAllTimestampsByDaySheetId(any(Long.class), any(String.class));
	}
}
