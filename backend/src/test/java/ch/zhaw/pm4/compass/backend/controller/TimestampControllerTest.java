package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.junit.Before;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	private DaySheetService daySheetService;
	@MockBean
	@SuppressWarnings("unused")
	private JwtDecoder jwtDecoder;
	static String token = "";
	private String reportText = "Testdate";

	DaySheet getDaySheet() {
		return new DaySheet(1l, reportText, LocalDate.now(), false);
	}

	DaySheetDto getDaySheetDto() {
		return new DaySheetDto(1l, reportText, LocalDate.now(), false);
	}

	private TimestampDto getTimestampDto() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"));
	}

	private TimestampDto getUpdateTimestamp() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("15:00:00"));
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingCreateTimestamp_ExpectCorrectReturn() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(getTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(getDaySheetDto());
		System.out.println("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
				+ "\", \"start_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}");
		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(getTimestamp.getStart_time().toString() + ":00"))
				.andExpect(jsonPath("$.end_time").value(getTimestamp.getEnd_time().toString() + ":00"));

		verify(timestampService, times(1)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingCreateTimestampOfNotExistingDaySheet_ExpectCorrectReturn() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(getTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(null);
		System.out.println("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
				+ "\", \"start_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}");
		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(timestampService, times(0)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingCreateTimestampOfConfirmedDaySheet_ExpectForbidden() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		DaySheetDto daySheet = getDaySheetDto();
		daySheet.setConfirmed(true);
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(getTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(daySheet);
		System.out.println("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
				+ "\", \"start_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}");
		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(timestampService, times(0)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingCreateTimestampAlreadyExists_ExpectDoesNotExists() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.createTimestamp(any(TimestampDto.class), any(String.class))).thenReturn(null);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(getDaySheetDto());
		// Act
		mockMvc.perform(post("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + getTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + getTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden())
				.andExpect(jsonPath("$").doesNotExist());

		verify(timestampService, times(1)).createTimestamp(any(TimestampDto.class), any(String.class));
	}


	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingUpdateTimestamp_ExpectCorrectResult() throws Exception {
		TimestampDto updateTimestamp = getUpdateTimestamp();
		when(timestampService.updateTimestampById(any(TimestampDto.class), any(String.class)))
				.thenReturn(updateTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(getDaySheetDto());

		// Act and Assert
		mockMvc.perform(put("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + updateTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + updateTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(updateTimestamp.getStart_time().toString() + ":00"))
				.andExpect(jsonPath("$.end_time").value(updateTimestamp.getEnd_time().toString() + ":00"));

		verify(timestampService, times(1)).updateTimestampById(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingUpdateTimestampOfNotExistingDaySheet_ExpectBadRequest() throws Exception {
		TimestampDto updateTimestamp = getUpdateTimestamp();
		when(timestampService.updateTimestampById(any(TimestampDto.class), any(String.class)))
				.thenReturn(updateTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(null);

		// Act and Assert
		mockMvc.perform(put("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + updateTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + updateTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(timestampService, times(0)).createTimestamp(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingUpdateTimestampOfConfirmedDaySheet_ExpectForbidden() throws Exception {
		DaySheetDto daySheet = getDaySheetDto();
		daySheet.setConfirmed(true);
		TimestampDto updateTimestamp = getUpdateTimestamp();
		when(timestampService.updateTimestampById(any(TimestampDto.class), any(String.class)))
				.thenReturn(updateTimestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(daySheet);

		// Act and Assert
		mockMvc.perform(put("/timestamp").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_sheet_id\": 1, \"start_time\": \"" + updateTimestamp.getStart_time().toString()
						+ "\", \"end_time\": \"" + updateTimestamp.getEnd_time().toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(timestampService, times(0)).updateTimestampById(any(TimestampDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingGetTimestampById_ExpectCorrectReturn() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		when(timestampService.getTimestampById(any(Long.class), any(String.class))).thenReturn(getTimestamp);

		// Act and Assert//
		mockMvc.perform(
				get("/timestamp/getById/" + getTimestamp.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.day_sheet_id").value(1l))
				.andExpect(jsonPath("$.start_time").value(getTimestamp.getStart_time().toString() + ":00"))
				.andExpect(jsonPath("$.end_time").value(getTimestamp.getEnd_time().toString() + ":00"));

		verify(timestampService, times(1)).getTimestampById(any(Long.class), any(String.class));
	}


	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingGetAllTimestampsByDayId_ExpectCorrectReturn() throws Exception {
		// Arrange
		TimestampDto getTimestamp = getTimestampDto();
		TimestampDto getTimestamp1 = getUpdateTimestamp();
		getUpdateTimestamp().setId(2l);
		getTimestamp1.setStart_time(LocalTime.parse("14:00:00"));
		getTimestamp1.setEnd_time(LocalTime.parse("15:00:00"));
		DaySheet daySheet = getDaySheet();

		daySheet.getTimestamps().add(new Timestamp(1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"), daySheet));
		daySheet.getTimestamps().add(new Timestamp(2l, LocalTime.parse("14:00:00"), LocalTime.parse("15:00:00"), daySheet));

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

	// todo til: fix please
	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingGetAllTimestampsByDayId_ExpectEmptyReturn() throws Exception {
		// Arrange
		DaySheet daySheet = getDaySheet();
		ArrayList<TimestampDto> timestamps = new ArrayList<TimestampDto>();
		when(timestampService.getAllTimestampsByDaySheetId(any(Long.class), any(String.class))).thenReturn(timestamps);

		// Act and Assert//.header("Authorization", "Bearer " + token))
		mockMvc.perform(
				get("/timestamp/allbydaysheetid/" + daySheet.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());
		verify(timestampService, times(1)).getAllTimestampsByDaySheetId(any(Long.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenCallingDeleteTimestamp_ExpectOk() throws Exception {
		// Arrange
		TimestampDto timestamp = getTimestampDto();
		DaySheetDto daySheet = getDaySheetDto();
		when(timestampService.getTimestampById(any(Long.class), any(String.class))).thenReturn(timestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(daySheet);

		// Act and Assert//.header("Authorization", "Bearer " + token))
		mockMvc.perform(delete("/timestamp/" + timestamp.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());
		verify(timestampService, times(1)).deleteTimestamp(any(Long.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenDeleteTimestampOfConfirmedDaySheet_ExpectForbidden() throws Exception {
		// Arrange
		TimestampDto timestamp = getTimestampDto();
		DaySheetDto daySheet = getDaySheetDto();
		daySheet.setConfirmed(true);
		when(timestampService.getTimestampById(any(Long.class), any(String.class))).thenReturn(timestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(daySheet);

		// Act and Assert//.header("Authorization", "Bearer " + token))
		mockMvc.perform(delete("/timestamp/" + timestamp.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isForbidden());
		verify(timestampService, times(0)).deleteTimestamp(any(Long.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void whenDeleteTimestampOfNotExistingDaySheet_ExpectBadRequest() throws Exception {
		// Arrange
		TimestampDto timestamp = getTimestampDto();
		DaySheetDto daySheet = getDaySheetDto();
		daySheet.setConfirmed(true);
		when(timestampService.getTimestampById(any(Long.class), any(String.class))).thenReturn(timestamp);
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(null);

		// Act and Assert//.header("Authorization", "Bearer " + token))
		mockMvc.perform(delete("/timestamp/" + timestamp.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isBadRequest());
		verify(timestampService, times(0)).deleteTimestamp(any(Long.class));
	}
}
