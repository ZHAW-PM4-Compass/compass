package ch.zhaw.pm4.compass.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.exception.TooManyRatingsPerCategoryException;
import ch.zhaw.pm4.compass.backend.exception.UserNotOwnerOfDaySheetException;
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

	public RatingDto createRating(RatingDto createRating)
			throws RatingIsNotValidException, CategoryNotFoundException, DaySheetNotFoundException {
		long categoryId = createRating.getCategory().getId();
		long daySheetId = createRating.getDaySheet().getId();
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		DaySheet daysheet = daySheetRepository.findById(daySheetId)
				.orElseThrow(() -> new DaySheetNotFoundException(daySheetId));

		Rating rating = convertDtoToEntity(createRating);
		if (!category.isValidRating(rating)) {
			throw new RatingIsNotValidException(rating, category);
		}
		rating.setCategory(category);
		rating.setDaySheet(daysheet);

		return convertEntityToDto(ratingRepository.save(rating));
	}

	public void recordCategoryRatings(List<CategoryDto> categoryDtoList, Long daySheetId, String userId,
			Boolean isSocialWorkerRating) throws TooManyRatingsPerCategoryException, RatingIsNotValidException,
			CategoryNotFoundException, DaySheetNotFoundException, UserNotOwnerOfDaySheetException {
		RatingType ratingRole = isSocialWorkerRating ? RatingType.SOCIAL_WORKER : RatingType.PARTICIPANT;
		DaySheet daySheet = daySheetRepository.findById(daySheetId)
				.orElseThrow(() -> new DaySheetNotFoundException(daySheetId));
		DaySheetDto daySheetDto = new DaySheetDto(daySheet.getId(), daySheet.getDayNotes(), daySheet.getDate(),
				daySheet.getConfirmed());

		boolean foundMoreThanOneRating = categoryDtoList.stream().anyMatch(i -> i.getMoodRatings().size() > 1);
		if (foundMoreThanOneRating) {
			throw new TooManyRatingsPerCategoryException();
		} else if (!daySheet.getUserId().equals(userId)) {
			throw new UserNotOwnerOfDaySheetException();
		}

		for (CategoryDto i : categoryDtoList) {
			RatingDto ratingFromCategory = i.getMoodRatings().getFirst();
			ratingFromCategory.setCategory(i);
			ratingFromCategory.setDaySheet(daySheetDto);
			ratingFromCategory.setRatingRole(ratingRole);
			this.createRating(ratingFromCategory);
		}
	}

	public List<RatingDto> getRatingsByDaySheet(Long daySheetId) throws DaySheetNotFoundException {
		DaySheet daySheet = daySheetRepository.findById(daySheetId)
				.orElseThrow(() -> new DaySheetNotFoundException(daySheetId));

		return daySheet.getMoodRatings().stream().map(i -> convertEntityToDto(i)).toList();
	}

	Rating convertDtoToEntity(RatingDto dto) {
		return new Rating(dto.getRating(), dto.getRatingRole());
	}

	RatingDto convertEntityToDto(Rating entity) {
		Category ratingCategory = entity.getCategory();
		CategoryDto categoryDto = new CategoryDto(ratingCategory.getId(), ratingCategory.getName(),
				ratingCategory.getMinimumValue(), ratingCategory.getMaximumValue());
		DaySheet daySheet = entity.getDaySheet();
		DaySheetDto daySheetDto = new DaySheetDto(daySheet.getId(), daySheet.getDayNotes(), daySheet.getDate(),
				daySheet.getConfirmed());
		RatingDto dto = new RatingDto(categoryDto, daySheetDto, entity.getRating(), entity.getRatingRole());
		return dto;
	}
}
