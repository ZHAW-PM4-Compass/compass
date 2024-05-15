package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.ParticipantDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;

public class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private UserService userService;

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

	private List<LocalUser> ownersEmpty = new ArrayList<>();
	private List<LocalUser> ownersFull = new ArrayList<>();
	private List<ParticipantDto> ownersDtoEmpty = new ArrayList<>();
	private List<ParticipantDto> ownersDtoFull = new ArrayList<>();

	private CategoryDto getCategoryDto() {
		return new CategoryDto(categoryId, categoryName, minValue, maxValue, ownersDtoEmpty);
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
		String partID = "secodn particip";
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
}
