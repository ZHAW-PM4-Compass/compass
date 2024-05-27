package ch.zhaw.pm4.compass.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.exception.TooManyRatingsPerCategoryException;
import ch.zhaw.pm4.compass.backend.exception.UserNotOwnerOfDaySheetException;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.ExtendedRatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.service.RatingService;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing ratings within the Compass application. 
 * Provides RESTful endpoints for creating and recording ratings related to day sheets and categories.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "Rating Controller", description = "Rating Endpoint")
@RestController
@RequestMapping("/rating")
public class RatingController {
	@Autowired
	private RatingService ratingService;
	@Autowired
	private UserService userService;

	/**
	 * Creates a new rating based on the provided rating DTO.
	 *
	 * @param rating The rating data transfer object containing the rating details.
	 * @return ResponseEntity with the created RatingDto or BAD_REQUEST if the rating is not valid.
	 * @throws RatingIsNotValidException if the rating criteria are not met.
	 * @throws CategoryNotFoundException if the category related to the rating does not exist.
	 * @throws DaySheetNotFoundException if the day sheet related to the rating does not exist.
	 */
	@PostMapping(produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto rating) {
		try {
			return ResponseEntity.ok(ratingService.createRating(rating));
		} catch (RatingIsNotValidException | CategoryNotFoundException | DaySheetNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Records multiple ratings related to a specific day sheet and user ID.
	 *
	 * @param daySheetId The day sheet ID where the ratings will be recorded.
	 * @param userId The user ID for whom the ratings are recorded.
	 * @param categoryDtoList List of category DTOs related to the ratings.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity with a list of recorded RatingDto or appropriate error status.
	 * @throws TooManyRatingsPerCategoryException if there are too many ratings for a single category.
	 * @throws UserNotOwnerOfDaySheetException if the user does not own the day sheet.
	 */
	@PostMapping(path = "/recordMoodRatingsByDaySheetIdAndUserId/{daySheetId}/{userId}", produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<List<RatingDto>> recordCategoryRatingsByDaySheetAndUserId(@PathVariable Long daySheetId,
			@PathVariable String userId, @RequestBody List<CategoryDto> categoryDtoList,
			Authentication authentication) {
		String callerId = authentication.getName();
		UserRole callingRole = userService.getUserRole(callerId);
		if (callingRole != UserRole.SOCIAL_WORKER && callingRole != UserRole.ADMIN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		try {
			ratingService.recordCategoryRatings(categoryDtoList, daySheetId, userId, true);
			return ResponseEntity.ok(ratingService.getRatingsByDaySheet(daySheetId));
		} catch (TooManyRatingsPerCategoryException | RatingIsNotValidException | CategoryNotFoundException
				| DaySheetNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (UserNotOwnerOfDaySheetException e) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Records multiple ratings related to a specific day sheet for the authenticated user.
	 *
	 * @param daySheetId The day sheet ID where the ratings will be recorded.
	 * @param categoryDtoList List of category DTOs related to the ratings.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity with a list of recorded RatingDto or appropriate error status.
	 * @throws TooManyRatingsPerCategoryException if there are too many ratings for a single category.
	 * @throws UserNotOwnerOfDaySheetException if the user does not own the day sheet.
	 */
	@PostMapping(path = "/recordMyMoodRatingsByDaySheetId/{daySheetId}", produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<List<RatingDto>> recordCategoryRatingsByDaySheetAndUserId(@PathVariable Long daySheetId,
			@RequestBody List<CategoryDto> categoryDtoList, Authentication authentication) {
		String callerId = authentication.getName();
		UserRole callingRole = userService.getUserRole(callerId);
		if (callingRole != UserRole.PARTICIPANT) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		try {
			ratingService.recordCategoryRatings(categoryDtoList, daySheetId, callerId, false);
			return ResponseEntity.ok(ratingService.getRatingsByDaySheet(daySheetId));
		} catch (TooManyRatingsPerCategoryException | RatingIsNotValidException | CategoryNotFoundException
				| DaySheetNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (UserNotOwnerOfDaySheetException e) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(path = "/getMoodRatingByDate/{date}", produces = "application/json")
	public ResponseEntity<List<ExtendedRatingDto>> getMoodRatingByDate(@PathVariable LocalDate date,
			@RequestParam Optional<String> userId) {
		List<ExtendedRatingDto> ratings = userId.isPresent() ? ratingService.getRatingsByDate(date, userId.get())
				: ratingService.getRatingsByDate(date);
		return ResponseEntity.ok(ratings);
	}
}
