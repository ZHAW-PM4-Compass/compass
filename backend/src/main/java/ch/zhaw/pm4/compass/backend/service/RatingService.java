package ch.zhaw.pm4.compass.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;

@Service
public class RatingService {
	@Autowired
	private RatingRepository ratingRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private DaySheetService daySheetService;

	public RatingDto createRating(RatingDto createRating)
			throws RatingIsNotValidException, CategoryNotFoundException, DaySheetNotFoundException {
		long categoryId = createRating.getCategory().getId();
		long dayShettId = createRating.getDaySheet().getId();
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		DaySheet daysheet = daySheetRepository.findById(dayShettId)
				.orElseThrow(() -> new DaySheetNotFoundException(dayShettId));

		Rating rating = convertDtoToEntity(createRating);
		if (!category.isValidRating(rating)) {
			throw new RatingIsNotValidException(rating, category);
		}
		rating.setCategory(category);
		rating.setDaySheet(daysheet);

		return convertEntityToDto(ratingRepository.save(rating));
	}

	private Rating convertDtoToEntity(RatingDto dto) {
		return new Rating(dto.getRating(), dto.getRatingRole());
	}

	private RatingDto convertEntityToDto(Rating entity) {
		CategoryDto categoryDto = categoryService.convertEntityToDto(entity.getCategory(), false);
		DaySheetDto daySheetDto = daySheetService.convertDaySheetToDaySheetDto(entity.getDaySheet());
		RatingDto dto = new RatingDto(categoryDto, daySheetDto, entity.getRating(), entity.getRatingRole());
		return dto;
	}
}
