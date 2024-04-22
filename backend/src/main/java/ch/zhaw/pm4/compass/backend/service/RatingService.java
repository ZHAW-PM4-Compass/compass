package ch.zhaw.pm4.compass.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;

@Service
public class RatingService {
	@Autowired
	private RatingRepository ratingRepository;
	@Autowired
	private CategoryService categoryService;

	public RatingDto createRating(RatingDto createRating) {
		Rating rating = convertDtoToEntity(createRating);
		return convertEntityToDto(ratingRepository.save(rating));
	}

	private Rating convertDtoToEntity(RatingDto dto) {
		DaySheet ds = new DaySheet();
		Category category = categoryService.convertDtoToEntity(dto.getCategory());
		return new Rating(dto.getRating(), dto.getRatingRole(), category, ds);
	}

	private RatingDto convertEntityToDto(Rating entity) {
		CategoryDto categoryDto = categoryService.convertEntityToDto(entity.getCategory(), false);
		RatingDto dto = new RatingDto(categoryDto, entity.getRating(), entity.getRatingRole());
		return dto;
	}
}
