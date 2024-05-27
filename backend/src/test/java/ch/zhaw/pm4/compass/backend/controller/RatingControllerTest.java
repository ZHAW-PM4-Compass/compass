package ch.zhaw.pm4.compass.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;

import ch.zhaw.pm4.compass.backend.GsonExclusionStrategy;
import ch.zhaw.pm4.compass.backend.LocalDateDeserializer;
import ch.zhaw.pm4.compass.backend.LocalDateSerializer;
import ch.zhaw.pm4.compass.backend.LocalTimeDeserializer;
import ch.zhaw.pm4.compass.backend.LocalTimeSerializer;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.service.RatingService;
import ch.zhaw.pm4.compass.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
public class RatingControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private UserService userService;

	@MockBean
	private RatingService ratingService;

	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
			.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
			.addDeserializationExclusionStrategy(new GsonExclusionStrategy())
			.addDeserializationExclusionStrategy(new GsonExclusionStrategy()).create();

	private DaySheet daySheet;
	private DaySheetDto daySheetDto;

	private Category categoryGlobal;
	private Category categoryPersonal;
	private CategoryDto categoryGlobalDto;
	private CategoryDto categoryPersonalDto;

	private RatingDto ratingOneCategoryGlobalDto;
	private RatingDto ratingTwoCategoryGlobalDto;
	private RatingDto ratingOneCategoryPersonalDto;

	private String userId = "dasfdwssdio";
	private LocalUser participant = new LocalUser(userId, UserRole.PARTICIPANT);

	private RatingDto getRatingOneDto() {
		ratingOneCategoryGlobalDto = new RatingDto();
		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		return ratingOneCategoryGlobalDto;
	}

	@BeforeEach
	public void setUpDtos() throws NotValidCategoryOwnerException {
		List<LocalUser> categoryOwners = Arrays.asList(this.participant);

		LocalDate now = LocalDate.now();
		daySheet = new DaySheet(1l, "", now, false);
		daySheet.setOwner(participant);
		daySheetDto = new DaySheetDto(1l, "", now, false);

		categoryGlobal = new Category("Unit Test", 0, 10, List.of());
		categoryGlobal.setId(1l);
		categoryGlobalDto = new CategoryDto(1l, "Unit Test", 0, 10);

		categoryPersonal = new Category("Integration Test", 0, 2, categoryOwners);
		categoryPersonal.setId(2l);
		categoryPersonalDto = new CategoryDto(2l, "Integration Test", 0, 2);

		ratingTwoCategoryGlobalDto = new RatingDto();
		ratingOneCategoryPersonalDto = new RatingDto();
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingPostWithBadData_expectBadRequest() throws Exception {
		when(ratingService.createRating(any(RatingDto.class))).thenThrow(new RatingIsNotValidException(categoryGlobal));

		mockMvc.perform(post("/rating").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getRatingOneDto(), RatingDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(ratingService.createRating(any(RatingDto.class))).thenThrow(new CategoryNotFoundException(30l));

		mockMvc.perform(post("/rating").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getRatingOneDto(), RatingDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(ratingService.createRating(any(RatingDto.class))).thenThrow(new DaySheetNotFoundException(1917l));
		mockMvc.perform(post("/rating").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getRatingOneDto(), RatingDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(ratingService, times(3)).createRating(any(RatingDto.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingPost_expectReturnOK() throws Exception {
		RatingDto rating = getRatingOneDto();
		rating.getCategory().setCategoryOwners(null);
		when(ratingService.createRating(any(RatingDto.class))).thenReturn(rating);

		mockMvc.perform(post("/rating").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(rating, RatingDto.class)).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.category").value(rating.getCategory()))
				.andExpect(jsonPath("$.rating").value(rating.getRating()))
				.andExpect(jsonPath("$.ratingRole").value(rating.getRatingRole()));

		verify(ratingService, times(1)).createRating(any(RatingDto.class));
	}

}
