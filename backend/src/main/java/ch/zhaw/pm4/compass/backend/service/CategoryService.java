package ch.zhaw.pm4.compass.backend.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private DaySheetService daySheetService;

	public CategoryDto createCategory(CategoryDto createCategory) throws CategoryAlreadyExistsException {
		Category category = convertDtoToEntity(createCategory);
		if (categoryRepository.findByName(category.getName()).isPresent()) {
			throw new CategoryAlreadyExistsException(category);
		}
		return convertEntityToDto(categoryRepository.save(category), false);
	}

	public CategoryDto getCategoryByName(String name, Boolean withRatings) throws NoSuchElementException {
		return convertEntityToDto(categoryRepository.findByName(name).orElseThrow(), withRatings);
	}

	public Category convertDtoToEntity(CategoryDto dto) {
		return new Category(dto.getName(), dto.getMinimumValue(), dto.getMaximumValue());
	}

	public CategoryDto convertEntityToDto(Category entity, Boolean withRatings) {
		CategoryDto dto = new CategoryDto(entity.getId(), entity.getName(), entity.getMinimumValue(),
				entity.getMaximumValue());
		if (withRatings) {
			List<RatingDto> ratingDtoList = entity.getMoodRatings().stream().map(i -> new RatingDto(dto,
					daySheetService.convertDaySheetToDaySheetDto(i.getDaySheet()), i.getRating(), i.getRatingRole()))
					.toList();
			dto.setMoodRatings(ratingDtoList);
		}
		return dto;
	}
}
