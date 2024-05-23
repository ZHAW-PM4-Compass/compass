package ch.zhaw.pm4.compass.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.UserIsNotParticipantException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.ParticipantDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private DaySheetService daySheetService;
	@Autowired
	private UserService userService;

	public CategoryDto createCategory(CategoryDto createCategory)
			throws CategoryAlreadyExistsException, NotValidCategoryOwnerException {
		Category category = convertDtoToEntity(createCategory);
		if (categoryRepository.findByName(category.getName()).isPresent()) {
			throw new CategoryAlreadyExistsException(category);
		}
		return convertEntityToDto(categoryRepository.save(category), false);
	}

	public CategoryDto getCategoryByName(String name, Boolean withRatings) throws NoSuchElementException {
		return convertEntityToDto(categoryRepository.findByName(name).orElseThrow(), withRatings);
	}

	public List<CategoryDto> getAllCategories() {
		return categoryRepository.findAll().stream().map(i -> convertEntityToDto(i, false)).toList();
	}

	public CategoryDto linkUsersToExistingCategory(CategoryDto linkCategory)
			throws NotValidCategoryOwnerException, GlobalCategoryException {
		Category newCategoryConfig = convertDtoToEntity(linkCategory);
		Category savedCategory = categoryRepository.findById(linkCategory.getId()).orElseThrow();
		if (savedCategory.getCategoryOwners().isEmpty()) {
			throw new GlobalCategoryException();
		}
		for (LocalUser i : newCategoryConfig.getCategoryOwners()) {
			if (!savedCategory.getCategoryOwners().contains(i)) {
				savedCategory.getCategoryOwners().add(i);
			}
		}

		return convertEntityToDto(categoryRepository.save(savedCategory), false);
	}

	public List<CategoryDto> getCategoryListByUserId(String userId) throws UserIsNotParticipantException {
		LocalUser user = userService.getLocalUser(userId);
		if (user.getRole() != UserRole.PARTICIPANT) {
			throw new UserIsNotParticipantException();
		}

		Iterable<Category> globalCategories = categoryRepository.findGlobalCategories();
		Iterable<Category> userCategories = categoryRepository.findAllByCategoryOwners(user);

		return Stream.concat(StreamSupport.stream(globalCategories.spliterator(), false),
				StreamSupport.stream(userCategories.spliterator(), false)).map(i -> {
					CategoryDto categoryDto = convertEntityToDto(i, false);
					categoryDto.setCategoryOwners(List.of());
					return categoryDto;
				}).toList();
	}

	public Category convertDtoToEntity(CategoryDto dto) throws NotValidCategoryOwnerException {
		List<LocalUser> categoryOwners = dto.getCategoryOwners().stream().map(t -> {
			UserDto i = userService.getUserById(t.getId());
			return new LocalUser(i.getUser_id(), i.getRole());
		}).toList();
		return new Category(dto.getName(), dto.getMinimumValue(), dto.getMaximumValue(), categoryOwners);
	}

	public CategoryDto convertEntityToDto(Category entity, Boolean withRatings) {
		List<ParticipantDto> categoryOwnersDto = entity.getCategoryOwners().stream()
				.map(t -> new ParticipantDto(t.getId())).toList();
		CategoryDto dto = new CategoryDto(entity.getId(), entity.getName(), entity.getMinimumValue(),
				entity.getMaximumValue(), categoryOwnersDto);
		List<RatingDto> ratingDtoList = List.of();
		if (withRatings) {
			ratingDtoList = entity.getMoodRatings().stream().map(i -> new RatingDto(dto,
					daySheetService.convertDaySheetToDaySheetDto(i.getDaySheet(), null), i.getRating(), i.getRatingRole()))
					.toList();
		}
		dto.setMoodRatings(ratingDtoList);
		return dto;
	}
}
