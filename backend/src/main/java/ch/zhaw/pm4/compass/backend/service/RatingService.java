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

/**
 * Service class for managing ratings within the system.
 * This service handles the creation, retrieval, and validation of ratings for categories within day sheets,
 * adhering to specific business rules and data integrity requirements.
 *
 * Using {@link RatingRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
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

	/**
	 * Creates a new rating based on the provided DTO. It validates the rating against the category
	 * rules and associates it with the specified day sheet.
	 *
	 * @param createRating The rating DTO to create.
	 * @return The newly created rating DTO.
	 * @throws RatingIsNotValidException If the rating is not within the valid range for the category.
	 * @throws CategoryNotFoundException If the category specified does not exist.
	 * @throws DaySheetNotFoundException If the day sheet specified does not exist.
	 */
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
			throw new RatingIsNotValidException(category);
		}
		rating.setCategory(category);
		rating.setDaySheet(daysheet);

		return convertEntityToDto(ratingRepository.save(rating));
	}

	/**
	 * Records multiple category ratings for a day sheet. Ensures that only one rating per category is recorded
	 * unless explicitly allowed and checks if the user is the owner of the day sheet.
	 *
	 * @param categoryDtoList List of category DTOs with their respective ratings.
	 * @param daySheetId ID of the day sheet where ratings are to be recorded.
	 * @param userId ID of the user recording the ratings.
	 * @param isSocialWorkerRating Indicates if the ratings are being recorded by a social worker.
	 * @throws TooManyRatingsPerCategoryException If multiple ratings for a single category are attempted to be recorded.
	 * @throws RatingIsNotValidException If a rating does not meet the category's specified range.
	 * @throws CategoryNotFoundException If any specified category does not exist.
	 * @throws DaySheetNotFoundException If the specified day sheet does not exist.
	 * @throws UserNotOwnerOfDaySheetException If the user is not the owner of the day sheet and tries to record ratings.
	 */
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

	/**
	 * Retrieves all ratings for a specific date. Optionally, ratings can also be
	 * filtered by user ID. The method returns a list of extended rating DTOs, which
	 * include the participant's name.
	 * 
	 * @param date The date for which ratings are to be retrieved.
	 * @param userId Optional user ID to filter ratings by.
	 * @return A list of extended rating DTOs containing the ratings for the specified date.
	 */
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

	/**
	 * Retrieves all ratings associated with a specific day sheet.
	 *
	 * @param daySheetId The ID of the day sheet for which ratings are to be retrieved.
	 * @return A list of rating DTOs.
	 * @throws DaySheetNotFoundException If the day sheet does not exist.
	 */
	public List<RatingDto> getRatingsByDaySheet(Long daySheetId) throws DaySheetNotFoundException {
		DaySheet daySheet = daySheetRepository.findById(daySheetId)
				.orElseThrow(() -> new DaySheetNotFoundException(daySheetId));

		return daySheet.getMoodRatings().stream().map(i -> convertEntityToDto(i)).toList();
	}

	/**
	 * Converts a {@link RatingDto} to a {@link Rating} entity. This method is crucial for persisting
	 * rating information obtained from the API into the database.
	 *
	 * @param dto The {@link RatingDto} object containing the details to be converted into an entity.
	 * @return A {@link Rating} entity with values set from the {@link RatingDto}.
	 */
	Rating convertDtoToEntity(RatingDto dto) {
		return new Rating(dto.getRating(), dto.getRatingRole());
	}

	/**
	 * Converts a {@link Rating} entity back to a {@link RatingDto}. This conversion includes
	 * assembling related data such as the category and day sheet details into the DTO for comprehensive data transfer.
   * 
	 * @param entity The {@link Rating} entity to be converted.
	 * @return A {@link RatingDto} containing the details from the entity and its related data.
	 */
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
