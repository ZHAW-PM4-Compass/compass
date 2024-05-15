package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.GsonExclusionStrategy;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.IncidentService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class IncidentControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private IncidentService incidentService;

	private LocalDate dateNow = LocalDate.now();

	private UserDto getUserDto() {
		return new UserDto("auth0|23sdfyl22ffowqpmclblrtkwerwsdff", "Test", "User", "test.user@stadtmuur.ch",
				UserRole.PARTICIPANT, false);
	}

	private IncidentDto getIncidentDto() {
		return new IncidentDto(1l, "Ausfall", "Teilnehmer kam nicht zur Arbeit", null, getUserDto());
	}

	@Before()
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateIncident() throws Exception {
		// Arrange
		when(incidentService.createIncident(any(IncidentDto.class))).thenReturn(getIncidentDto());
		Gson gson = new GsonBuilder()
				.addSerializationExclusionStrategy(new GsonExclusionStrategy())
				.addDeserializationExclusionStrategy(new GsonExclusionStrategy())
				.create();

		// Act and Assert//
		mockMvc.perform(post("/incident").contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getIncidentDto().getId()))
				.andExpect(jsonPath("$.title").value(getIncidentDto().getTitle()))
				.andExpect(jsonPath("$.description").value(getIncidentDto().getDescription()))
				.andExpect(jsonPath("$.user").value(getIncidentDto().getUser()));

		verify(incidentService, times(1)).createIncident(any(IncidentDto.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testUpdateIncident() throws Exception {
		// Arrange
		when(incidentService.updateIncident(any(IncidentDto.class))).thenReturn(getIncidentDto());
		Gson gson = new GsonBuilder()
				.addSerializationExclusionStrategy(new GsonExclusionStrategy())
				.addDeserializationExclusionStrategy(new GsonExclusionStrategy())
				.create();

		// Act and Assert//
		mockMvc.perform(put("/incident").contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(getIncidentDto(), IncidentDto.class))
						.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getIncidentDto().getId()))
				.andExpect(jsonPath("$.title").value(getIncidentDto().getTitle()))
				.andExpect(jsonPath("$.description").value(getIncidentDto().getDescription()))
				.andExpect(jsonPath("$.user").value(getIncidentDto().getUser()));

		verify(incidentService, times(1)).updateIncident(any(IncidentDto.class));

	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void testDeleteIncident() throws Exception {
		doNothing().when(incidentService).deleteIncident(any(Long.class));
		// Act and Assert//
		mockMvc.perform(delete("/incident/1")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk());

		verify(incidentService, times(1)).deleteIncident(any(Long.class));
	}



	@Test
	@WithMockUser(username = "testuser", roles = {})
	void getGetAll() throws Exception {
		// Arrange
		List<IncidentDto> incidentDtoList = new ArrayList<>();
		incidentDtoList.add(getIncidentDto());

		Gson gson = new GsonBuilder()
				.addSerializationExclusionStrategy(new GsonExclusionStrategy())
				.addDeserializationExclusionStrategy(new GsonExclusionStrategy())
				.create();

		when(incidentService.getAll(any(String.class))).thenReturn(incidentDtoList);

		// Act and Assert/
		String res = mockMvc.perform(get("/incident/getAll")
						.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<IncidentDto> resultIncidentDtoList = gson.fromJson(res, new TypeToken<List<IncidentDto>>() {
		}.getType());

		assertEquals(incidentDtoList.getFirst().getId(), resultIncidentDtoList.getFirst().getId());
		assertEquals(incidentDtoList.getFirst().getTitle(), resultIncidentDtoList.getFirst().getTitle());
		assertEquals(incidentDtoList.getFirst().getDescription(), resultIncidentDtoList.getFirst().getDescription());
		assertEquals(incidentDtoList.getFirst().getUser(), resultIncidentDtoList.getFirst().getUser());

		verify(incidentService, times(1)).getAll(any(String.class));
	}

}
