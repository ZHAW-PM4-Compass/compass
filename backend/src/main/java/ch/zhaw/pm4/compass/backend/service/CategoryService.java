package ch.zhaw.pm4.compass.backend.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
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
		if (withRatings) {
			List<RatingDto> ratingDtoList = entity.getMoodRatings().stream().map(i -> new RatingDto(dto,
					daySheetService.convertDaySheetToDaySheetDto(i.getDaySheet()), i.getRating(), i.getRatingRole()))
					.toList();
			dto.setMoodRatings(ratingDtoList);
		}
		return dto;
	}
}
