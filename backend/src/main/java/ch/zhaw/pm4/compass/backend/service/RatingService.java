package ch.zhaw.pm4.compass.backend.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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
import ch.zhaw.pm4.compass.backend.model.dto.ExtendedRatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
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
	private UserService userService;

	private Map<String, String> userNames;

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
		} else if (!daySheet.getOwner().getId().equals(userId)) {
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

	public List<ExtendedRatingDto> getRatingsByDate(LocalDate date, String... userId) {
		List<DaySheet> daySheets;
		if (userId.length == 0) {
			daySheets = daySheetRepository.findAllByDate(date);
		} else if (userId.length == 1) {
			Optional<DaySheet> userDaySheet = daySheetRepository.findByDateAndOwnerId(date, userId[0]);
			daySheets = userDaySheet.isPresent() ? List.of(userDaySheet.get()) : List.of();
		} else {
			throw new IllegalArgumentException("More than one user ID");
		}

		List<UserDto> allParticipants = userService.getAllParticipants();
		userNames = new HashMap<String, String>();
		for (UserDto i : allParticipants) {
			String name = (i.getGiven_name() + " " + i.getFamily_name()).trim();
			userNames.put(i.getUser_id(), name);
		}

		return daySheets.stream().mapMulti(this::expandDaySheetToExtendedRatings).toList();
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

	private void expandDaySheetToExtendedRatings(DaySheet daySheet, Consumer<ExtendedRatingDto> c) {
		String userId = daySheet.getOwner().getId();
		String name = userNames.containsKey(userId) ? userNames.get(userId) : "k.A";
		daySheet.getMoodRatings().stream().forEach(i -> {
			ExtendedRatingDto extendedRating = new ExtendedRatingDto();
			extendedRating.setDate(daySheet.getDate());
			extendedRating.setParticipantName(name);
			RatingDto rating = convertEntityToDto(i);
			extendedRating.setRating(rating);
			c.accept(extendedRating);
		});
	}
}
