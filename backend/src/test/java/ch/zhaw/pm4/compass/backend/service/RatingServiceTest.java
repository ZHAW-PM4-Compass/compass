package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import ch.zhaw.pm4.compass.backend.model.dto.ExtendedRatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
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

	@Mock
	private UserService userService;

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

	private ExtendedRatingDto extendedRatingOneCaregoryGlobalDto;
	private ExtendedRatingDto extendedRatingTwoCaregoryGlobalDto;
	private ExtendedRatingDto extendedRatingOneCaregoryPersonalDto;

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

		extendedRatingOneCaregoryGlobalDto = new ExtendedRatingDto();
		extendedRatingTwoCaregoryGlobalDto = new ExtendedRatingDto();
		extendedRatingOneCaregoryPersonalDto = new ExtendedRatingDto();

		extendedRatingOneCaregoryGlobalDto.setRating(ratingOneCategoryGlobalDto);
		extendedRatingOneCaregoryGlobalDto.setDate(now);
		extendedRatingOneCaregoryGlobalDto.setParticipantName("Chester");

		extendedRatingTwoCaregoryGlobalDto.setRating(ratingTwoCategoryGlobalDto);
		extendedRatingTwoCaregoryGlobalDto.setDate(now);
		extendedRatingTwoCaregoryGlobalDto.setParticipantName("Tester McTester");

		extendedRatingOneCaregoryPersonalDto.setRating(ratingOneCategoryPersonalDto);
		extendedRatingOneCaregoryPersonalDto.setDate(now);
		extendedRatingOneCaregoryPersonalDto.setParticipantName("Chester");
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
	public void whenGettingRatingsByDateByMoreThanOneUser_expectIllegalArgumentException() {
		String[] userIds = new String[] { "id1", "id2" };
		assertThrows(IllegalArgumentException.class, () -> ratingService.getRatingsByDate(LocalDate.now(), userIds));
	}

	@Test
	public void whenGettingRatingsByDate_expectListOfExpandedRatingDtos() {
		String userIdTwo = "dm,xnciouoi";
		UserDto userDtoOne = new UserDto(userId, "testmail", "Chester", "", null, UserRole.PARTICIPANT);
		UserDto userDtoTwo = new UserDto(userIdTwo, "testmail2", "Tester", "McTester", null, UserRole.PARTICIPANT);

		LocalUser participantTwo = new LocalUser(userIdTwo, UserRole.PARTICIPANT);
		LocalDate now = LocalDate.now();
		DaySheet daySheetTwo = new DaySheet(2l, "", now, false);
		daySheetTwo.setOwner(participantTwo);

		Rating ratingSavedOne = new Rating(3, RatingType.PARTICIPANT);
		ratingSavedOne.setCategory(categoryGlobal);
		ratingSavedOne.setDaySheet(daySheet);
		Rating ratingSavedTwo = new Rating(2, RatingType.PARTICIPANT);
		ratingSavedTwo.setCategory(categoryPersonal);
		ratingSavedTwo.setDaySheet(daySheet);
		Rating ratingSavedThree = new Rating(6, RatingType.PARTICIPANT);
		ratingSavedThree.setCategory(categoryGlobal);
		ratingSavedThree.setDaySheet(daySheetTwo);

		List<UserDto> allParticipants = List.of(userDtoOne, userDtoTwo);

		List<ExtendedRatingDto> expectedRatings = List.of(extendedRatingOneCaregoryGlobalDto,
				extendedRatingOneCaregoryPersonalDto, extendedRatingTwoCaregoryGlobalDto);
		List<Rating> moodRatingsReturnOne = List.of(ratingSavedOne, ratingSavedTwo);
		List<Rating> moodRatingsReturnTwo = List.of(ratingSavedThree);

		daySheet.setMoodRatings(moodRatingsReturnOne);
		daySheetTwo.setMoodRatings(moodRatingsReturnTwo);
		List<DaySheet> daySheets = List.of(daySheet, daySheetTwo);

		when(userService.getAllParticipants()).thenReturn(allParticipants);
		when(daySheetRepository.findAllByDate(any(LocalDate.class))).thenReturn(daySheets);
		when(daySheetRepository.findByDateAndOwnerId(any(LocalDate.class), eq(userIdTwo)))
				.thenReturn(Optional.of(daySheetTwo));

		List<ExtendedRatingDto> returnRatings = ratingService.getRatingsByDate(now);

		assertEquals(expectedRatings.size(), returnRatings.size());

		for (int i = 0; i < expectedRatings.size(); i++) {
			assertEquals(expectedRatings.get(i).getRating().getCategory().getId(),
					returnRatings.get(i).getRating().getCategory().getId());
			assertEquals(expectedRatings.get(i).getRating().getRating(), returnRatings.get(i).getRating().getRating());
			assertEquals(expectedRatings.get(i).getRating().getRatingRole(),
					returnRatings.get(i).getRating().getRatingRole());
			assertEquals(expectedRatings.get(i).getParticipantName(), returnRatings.get(i).getParticipantName());
		}

		List<ExtendedRatingDto> returnOneRating = ratingService.getRatingsByDate(now, userIdTwo);
		assertEquals(1, returnOneRating.size());
		assertEquals(ratingSavedThree.getCategory().getId(), returnOneRating.get(0).getRating().getCategory().getId());
		assertEquals(ratingSavedThree.getRating(), returnOneRating.get(0).getRating().getRating());
		assertEquals(ratingSavedThree.getRatingRole(), returnOneRating.get(0).getRating().getRatingRole());
		assertEquals(userDtoTwo.getGiven_name() + " " + userDtoTwo.getFamily_name(),
				returnOneRating.get(0).getParticipantName());
	}

	@Test
	public void whenRecordingCategoryRatingsforNonExistantDaySheet_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto));
		categoryPersonalDto.setMoodRatings(Arrays.asList(ratingOneCategoryPersonalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto, categoryPersonalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(DaySheetNotFoundException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1l, userId, false));
	}

	@Test
	public void whenRecordingCategoryRatingsWithMoreThanOneRating_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto, ratingTwoCategoryGlobalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));

		assertThrows(TooManyRatingsPerCategoryException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1l, userId, false));
	}

	@Test
	void whenTryingToRecordRatingForDaySheetWithUserIdOfNotOwner_expectException() {
		categoryGlobalDto.setMoodRatings(Arrays.asList(ratingOneCategoryGlobalDto));
		List<CategoryDto> categoriesWithRatings = Arrays.asList(categoryGlobalDto);

		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));

		assertThrows(UserNotOwnerOfDaySheetException.class,
				() -> ratingService.recordCategoryRatings(categoriesWithRatings, 1l, userId + "test", false));

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

		ratingService.recordCategoryRatings(categoriesWithRatings, 1l, userId, false);

		verify(ratingService, times(2)).createRating(any(RatingDto.class));
	}
}
