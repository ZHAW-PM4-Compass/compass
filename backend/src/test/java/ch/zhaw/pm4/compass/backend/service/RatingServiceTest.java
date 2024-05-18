package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.exception.TooManyRatingsPerCategoryException;
import ch.zhaw.pm4.compass.backend.exception.UserNotOwnerOfDaySheetException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;

public class RatingServiceTest {
	@Mock
	private RatingRepository ratingRepository;
	@Mock
	private DaySheetRepository daySheetRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@Spy
	@InjectMocks
	private RatingService ratingService;

	private DaySheet daySheet;
	private DaySheetDto daySheetDto;

	private Category categoryGlobal;
	private Category categoryPersonal;
	private CategoryDto categoryGlobalDto;
	private CategoryDto categoryPersonalDto;

	private RatingDto ratingOneCategoryGlobalDto;
	private RatingDto ratingTwoCategoryGlobalDto;
	private RatingDto ratingOneCategoryPersonalDto;

	private String userId;
	private LocalUser participant;

	@BeforeEach
	void setUp() throws NotValidCategoryOwnerException {
		MockitoAnnotations.openMocks(this);

		userId = "dasfdwssdio";
		participant = new LocalUser(userId, UserRole.PARTICIPANT);
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

		ratingOneCategoryGlobalDto = new RatingDto();
		ratingTwoCategoryGlobalDto = new RatingDto();
		ratingOneCategoryPersonalDto = new RatingDto();

		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		ratingTwoCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingTwoCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingTwoCategoryGlobalDto.setRating(6);
		ratingTwoCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		ratingOneCategoryPersonalDto.setCategory(categoryPersonalDto);
		ratingOneCategoryPersonalDto.setDaySheet(daySheetDto);
		ratingOneCategoryPersonalDto.setRating(2);
		ratingOneCategoryPersonalDto.setRatingRole(RatingType.PARTICIPANT);
	}

	@Test
	public void whenCreatingRatingForNonExistantDaySheet_expectException() {
		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(DaySheetNotFoundException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingRatingForNonExistantCategory_expectException() {
		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingInvalidRating_expectException() {
		ratingOneCategoryGlobalDto.setRating(300);

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));

		assertThrows(RatingIsNotValidException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingRating_expectSameRating()
			throws RatingIsNotValidException, CategoryNotFoundException, DaySheetNotFoundException {
		Rating ratingSaved = new Rating(3, RatingType.PARTICIPANT);
		ratingSaved.setCategory(categoryGlobal);
		ratingSaved.setDaySheet(daySheet);

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));
		when(ratingRepository.save(any(Rating.class))).thenReturn(ratingSaved);

		RatingDto resultRating = ratingService.createRating(ratingOneCategoryGlobalDto);

		assertEquals(ratingOneCategoryGlobalDto.getCategory().getId(), resultRating.getCategory().getId());
		assertEquals(ratingOneCategoryGlobalDto.getDaySheet().getId(), resultRating.getDaySheet().getId());
		assertEquals(ratingOneCategoryGlobalDto.getRating(), resultRating.getRating());
		assertEquals(ratingOneCategoryGlobalDto.getRatingRole(), resultRating.getRatingRole());
	}

	@Test
	public void whenGettingRatingsForNonExistantDaySheet_expectException() {
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(DaySheetNotFoundException.class, () -> ratingService.getRatingsByDaySheet(6l));
	}

	@Test
	public void whenGettingRatingsOfDaySheet_expectListOfRatingDtos() throws DaySheetNotFoundException {
		Rating ratingSavedOne = new Rating(3, RatingType.PARTICIPANT);
		ratingSavedOne.setCategory(categoryGlobal);
		ratingSavedOne.setDaySheet(daySheet);
		Rating ratingSavedTwo = new Rating(2, RatingType.PARTICIPANT);
		ratingSavedTwo.setCategory(categoryPersonal);
		ratingSavedTwo.setDaySheet(daySheet);

		List<RatingDto> expectedRatings = Arrays.asList(ratingOneCategoryGlobalDto, ratingOneCategoryPersonalDto);
		List<Rating> moodRatingsReturn = Arrays.asList(ratingSavedOne, ratingSavedTwo);

		daySheet.setMoodRatings(moodRatingsReturn);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));

		List<RatingDto> returnRatings = ratingService.getRatingsByDaySheet(1l);

		assertEquals(expectedRatings.size(), returnRatings.size());

		for (int i = 0; i < expectedRatings.size(); i++) {
			assertEquals(expectedRatings.get(i).getCategory().getId(), returnRatings.get(i).getCategory().getId());
			assertEquals(expectedRatings.get(i).getDaySheet().getId(), returnRatings.get(i).getDaySheet().getId());
			assertEquals(expectedRatings.get(i).getRating(), returnRatings.get(i).getRating());
			assertEquals(expectedRatings.get(i).getRatingRole(), returnRatings.get(i).getRatingRole());
		}
	}

	@Test
	public void whenRecordingCategoryRatingsforNonExistantDaySheet_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto));
		categoryPersonalDto.setMoodRatings(Arrays.asList(ratingOneCategoryPersonalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto, categoryPersonalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(DaySheetNotFoundException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1L, userId, false));
	}

	@Test
	public void whenRecordingCategoryRatingsWithMoreThanOneRating_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto, ratingTwoCategoryGlobalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));

		assertThrows(TooManyRatingsPerCategoryException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1L, userId, false));
	}

	@Test
	void whenTryingToRecordRatingForDaySheetWithUserIdOfNotOwner_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));

		assertThrows(UserNotOwnerOfDaySheetException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1L, userId + "test", false));

	}

	@Test
	void whenRecordingRatingsWithCorrectInput_expectCorrectNumberOfCallsForCreate()
			throws RatingIsNotValidException, CategoryNotFoundException, DaySheetNotFoundException,
			TooManyRatingsPerCategoryException, UserNotOwnerOfDaySheetException {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto));
		categoryPersonalDto.setMoodRatings(Arrays.asList(ratingOneCategoryPersonalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto, categoryPersonalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		doReturn(ratingOneCategoryGlobalDto).when(ratingService).createRating(any(RatingDto.class));

		ratingService.recordCategoryRatings(categoriesWithRatings, 1L, userId, false);

		verify(ratingService, times(2)).createRating(any(RatingDto.class));
	}
}
