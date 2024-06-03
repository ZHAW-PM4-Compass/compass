package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.UserIsNotParticipantException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.*;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private UserService userService;
	@Mock
	private DaySheetService daySheetService;

	@InjectMocks
	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		LocalUser localUser = new LocalUser("dcsdcdesec", UserRole.PARTICIPANT);
		ParticipantDto participantDto = new ParticipantDto("dcsdcdesec");

		ownersFull.add(localUser);
		ownersDtoFull.add(participantDto);
	}

	private long categoryId = 1l;
	private String categoryName = "Stress";
	private int minValue = 1;
	private int maxValue = 10;

	private List<LocalUser> ownersEmpty = List.of();
	private List<LocalUser> ownersFull = new ArrayList<>();
	private List<ParticipantDto> ownersDtoEmpty = List.of();
	private List<ParticipantDto> ownersDtoFull = new ArrayList<>();
	private List<RatingDto> moodRatingsEmpty = List.of();

	private CategoryDto getCategoryDto() {
		return new CategoryDto(categoryId, categoryName, minValue, maxValue, ownersDtoEmpty, moodRatingsEmpty);
	}

	private Category getCategory() throws NotValidCategoryOwnerException {
		Category category = new Category(categoryName, minValue, maxValue, ownersEmpty);
		category.setId(categoryId);
		return category;
	}

	@Test
	public void whenCreatingCategory_expectSameCategoryToBeReturned() throws Exception {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		CategoryDto resultCategory = categoryService.createCategory(createCategory);
		assertEquals(createCategory, resultCategory);
	}

	@Test
	public void whenCreatingCategoryThatExists_expectException() throws NotValidCategoryOwnerException {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
		assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(createCategory));
	}

	@Test
	public void whenGettingCategoryByName_expectCorrectCategory() throws NotValidCategoryOwnerException {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.findByName(createCategory.getName())).thenReturn(Optional.of(category));
		CategoryDto resultCategory = categoryService.getCategoryByName(categoryName, false);
		assertEquals(createCategory, resultCategory);
	}

	@Test
	public void whenGettingNonExistantCategoryName_expectException() {
		when(categoryRepository.findByName(any())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryByName(categoryName, false));
	}

	@Test
	public void whenTryingToAssignUserToCategoryWithNoUser_expectGlobalCategoryException()
			throws NotValidCategoryOwnerException {
		Category category = getCategory();
		category.setCategoryOwners(ownersEmpty);
		CategoryDto categoryDto = getCategoryDto();
		categoryDto.setCategoryOwners(ownersDtoFull);
		UserDto userDto = new UserDto(ownersDtoFull.getFirst().getId(), "", "", "", UserRole.PARTICIPANT, false);
		when(userService.getUserById(ownersDtoFull.getFirst().getId())).thenReturn(userDto);
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		assertThrows(GlobalCategoryException.class, () -> categoryService.linkUsersToExistingCategory(categoryDto));
	}

	@Test
	public void whenCreatingCategoryWithLinkedUsers_expectCategoryToHaveLinkedUsers()
			throws NotValidCategoryOwnerException, CategoryAlreadyExistsException {
		CategoryDto createCategory = getCategoryDto();
		createCategory.setCategoryOwners(ownersDtoFull);

		Category category = getCategory();
		category.setCategoryOwners(ownersFull);
		when(categoryRepository.save(any(Category.class))).thenReturn(category);

		UserDto userDto = new UserDto(ownersDtoFull.getFirst().getId(), "", "", "", UserRole.PARTICIPANT, false);
		when(userService.getUserById(userDto.getUser_id())).thenReturn(userDto);

		CategoryDto resultCategory = categoryService.createCategory(createCategory);
		assertEquals(createCategory.getId(), resultCategory.getId());
		assertEquals(createCategory.getName(), resultCategory.getName());
		assertEquals(createCategory.getCategoryOwners().size(), resultCategory.getCategoryOwners().size());
		for (int i = 0; i < createCategory.getCategoryOwners().size(); i++) {
			assertEquals(createCategory.getCategoryOwners().get(i).getId(),
					resultCategory.getCategoryOwners().get(i).getId());
		}
	}

	@Test
	public void whenCreatingCategoryWithLinkedNonParticipants_expectNotValidCategoryOwnerException()
			throws CategoryAlreadyExistsException {
		CategoryDto createCategory = getCategoryDto();
		createCategory.setCategoryOwners(ownersDtoFull);
		UserDto userDto = new UserDto(ownersDtoFull.getFirst().getId(), "", "", "", UserRole.SOCIAL_WORKER, false);
		when(userService.getUserById(userDto.getUser_id())).thenReturn(userDto);
		assertThrows(NotValidCategoryOwnerException.class, () -> categoryService.createCategory(createCategory));
	}

	@Test
	public void whenLinkingUsersToExistingPersonalCategory_expectSuccessfulLinking()
			throws NotValidCategoryOwnerException, CategoryAlreadyExistsException, GlobalCategoryException {
		String partID = "second particip";
		CategoryDto linkCategory = getCategoryDto();
		ownersDtoFull.add(new ParticipantDto(partID));
		linkCategory.setCategoryOwners(ownersDtoFull);

		UserDto userDto1 = new UserDto(ownersDtoFull.getFirst().getId(), "", "", "", UserRole.PARTICIPANT, false);
		when(userService.getUserById(userDto1.getUser_id())).thenReturn(userDto1);
		UserDto userDto2 = new UserDto(ownersDtoFull.getLast().getId(), "", "", "", UserRole.PARTICIPANT, false);
		when(userService.getUserById(userDto2.getUser_id())).thenReturn(userDto2);

		Category category = getCategory();
		category.setCategoryOwners(ownersFull);
		when(categoryRepository.findById(linkCategory.getId())).thenReturn(Optional.of(category));

		List<LocalUser> ownersFullTwo = new ArrayList<>(ownersFull);
		ownersFullTwo.add(new LocalUser(partID, UserRole.PARTICIPANT));
		Category categoryWithTwoParticipants = new Category(categoryName, minValue, maxValue, ownersFullTwo);
		categoryWithTwoParticipants.setId(categoryId);
		when(categoryRepository.save(any(Category.class))).thenReturn(categoryWithTwoParticipants);

		CategoryDto resultCategory = categoryService.linkUsersToExistingCategory(linkCategory);
		assertEquals(linkCategory.getId(), resultCategory.getId());
		assertEquals(linkCategory.getName(), resultCategory.getName());
		assertEquals(linkCategory.getCategoryOwners().size(), resultCategory.getCategoryOwners().size());
		for (int i = 0; i < linkCategory.getCategoryOwners().size(); i++) {
			assertEquals(linkCategory.getCategoryOwners().get(i).getId(),
					resultCategory.getCategoryOwners().get(i).getId());
		}
	}

	@Test
	public void whenGettingCategoryListByUserId_expectGlobalAndPersonalCategories()
			throws NotValidCategoryOwnerException, UserIsNotParticipantException {
		Category categoryGlobal = getCategory();
		CategoryDto categoryGlobalDto = getCategoryDto();

		Category categoryPersonal = getCategory();
		categoryPersonal.setId(2l);
		categoryPersonal.setName(categoryName + " Special");
		categoryPersonal.setCategoryOwners(ownersFull);
		CategoryDto categoryPersonalDto = getCategoryDto();
		categoryPersonalDto.setId(2l);
		categoryPersonalDto.setName(categoryName + " Special");
		categoryPersonalDto.setCategoryOwners(List.of());

		ArrayList<Category> globalCategories = new ArrayList<>();
		ArrayList<Category> personalCategories = new ArrayList<>();
		globalCategories.add(categoryGlobal);
		personalCategories.add(categoryPersonal);

		List<CategoryDto> expectedCategories = List.of(categoryGlobalDto, categoryPersonalDto);

		when(userService.getLocalUser(any(String.class))).thenReturn(categoryPersonal.getCategoryOwners().getFirst());
		when(categoryRepository.findGlobalCategories()).thenReturn(globalCategories);
		when(categoryRepository.findAllByCategoryOwners(categoryPersonal.getCategoryOwners().getFirst()))
				.thenReturn(personalCategories);

		List<CategoryDto> isList = categoryService
				.getCategoryListByUserId(categoryPersonal.getCategoryOwners().getFirst().getId());

		assertEquals(expectedCategories, isList);
	}

	@Test
	public void whenGettingCategoryListOfNonParticipant_expectException() {
		LocalUser user = new LocalUser("test", UserRole.SOCIAL_WORKER);

		when(userService.getLocalUser(any(String.class))).thenReturn(user);

		assertThrows(UserIsNotParticipantException.class, () -> categoryService.getCategoryListByUserId("test"));
	}

	@Test
	public void whenConvertingCategoryToDto_expectRatingFlagToBeRespected() throws NotValidCategoryOwnerException {
		Category categoryUnderTest = getCategory();
		Rating ratingOne = new Rating(5, RatingType.PARTICIPANT);
		Rating ratingTwo = new Rating(6, RatingType.SOCIAL_WORKER);
		List<Rating> ratingList = Arrays.asList(ratingOne, ratingTwo);
		categoryUnderTest.setMoodRatings(ratingList);

		DaySheet daySheet = new DaySheet(1l, "notes", LocalDate.now(), false);
		ratingOne.setDaySheet(daySheet);
		ratingTwo.setDaySheet(daySheet);
		DaySheetDto daySheetDto = new DaySheetDto(1l, "notes", LocalDate.now(), false);

		when(daySheetService.convertDaySheetToDaySheetDto(any(DaySheet.class), isNull(),any(String.class))).thenReturn(daySheetDto);

		CategoryDto dtoOne = categoryService.convertEntityToDto(categoryUnderTest, false);
		CategoryDto dtoTwo = categoryService.convertEntityToDto(categoryUnderTest, true);

		assertEquals(dtoOne.getId(), dtoTwo.getId(), categoryUnderTest.getId());
		assertEquals(dtoOne.getName(), dtoTwo.getName(), categoryUnderTest.getName());
		assertEquals(dtoOne.getMinimumValue(), dtoTwo.getMinimumValue(), categoryUnderTest.getMinimumValue());
		assertEquals(dtoOne.getMaximumValue(), dtoTwo.getMaximumValue(), categoryUnderTest.getMaximumValue());
		assertTrue(dtoOne.getMoodRatings().size() == 0);
		assertTrue(dtoTwo.getMoodRatings().size() == ratingList.size());

		List<RatingDto> ratingListDto = dtoTwo.getMoodRatings();
		for (int i = 0; i < ratingList.size(); i++) {
			assertEquals(ratingList.get(i).getRating(), ratingListDto.get(i).getRating());
			assertEquals(ratingList.get(i).getRatingRole(), ratingListDto.get(i).getRatingRole());
		}
		verify(daySheetService, times(2)).convertDaySheetToDaySheetDto(any(DaySheet.class), isNull(),any(String.class));
	}
}
