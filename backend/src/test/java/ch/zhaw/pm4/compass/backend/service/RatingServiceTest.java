package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.RatingIsNotValidException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;

public class RatingServiceTest {
	@Mock
	private RatingRepository ratingRepository;
	@Mock
	private DaySheetRepository daySheetRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private RatingService ratingService;

	private DaySheet daySheet;
	private DaySheetDto daySheetDto;

	private Category categoryGlobal;
	private Category categoryPersonal;
	private CategoryDto categoryGlobalDto;
	private CategoryDto categoryPersonalDto;

	private RatingDto ratingOneCategoryGlobalDto;
	private RatingDto ratingTwoCategoryGlobalDto;
	private RatingDto ratingOneCategoryPersonalDto;
	private RatingDto ratingTwoCategoryPersonalDto;

	private LocalUser participant;

	@BeforeEach
	void setUp() throws NotValidCategoryOwnerException {
		MockitoAnnotations.openMocks(this);

		this.participant = new LocalUser("dasfdwssdio", UserRole.PARTICIPANT);
		List<LocalUser> categoryOwners = Arrays.asList(this.participant);

		LocalDate now = LocalDate.now();
		this.daySheet = new DaySheet(1l, "", now, false);
		this.daySheetDto = new DaySheetDto(1l, "", now, false);

		this.categoryGlobal = new Category("Unit Test", 0, 10, List.of());
		this.categoryGlobal.setId(1l);
		this.categoryGlobalDto = new CategoryDto(1l, "Unit Test", 0, 10);

		this.categoryPersonal = new Category("Integration Test", 0, 2, categoryOwners);
		this.categoryPersonal.setId(2l);
		this.categoryPersonalDto = new CategoryDto(2l, "Integration Test", 0, 2);

		this.ratingOneCategoryGlobalDto = new RatingDto();
		this.ratingTwoCategoryGlobalDto = new RatingDto();
		this.ratingOneCategoryPersonalDto = new RatingDto();
		this.ratingTwoCategoryPersonalDto = new RatingDto();
	}

	@Test
	public void whenCreatingRatingForNonExistantDaySheet_expectException() {
		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(DaySheetNotFoundException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingRatingForNonCategory_expectException() {
		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingInvalidRating_expectException() {
		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(300);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));

		assertThrows(RatingIsNotValidException.class, () -> ratingService.createRating(ratingOneCategoryGlobalDto));
	}

	@Test
	public void whenCreatingRating_ExpectSameRating()
			throws RatingIsNotValidException, CategoryNotFoundException, DaySheetNotFoundException {
		Rating ratingSaved = new Rating(3, RatingType.PARTICIPANT);
		ratingSaved.setCategory(categoryGlobal);
		ratingSaved.setDaySheet(daySheet);

		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		when(daySheetRepository.findById(1l)).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(1l)).thenReturn(Optional.of(categoryGlobal));
		when(ratingRepository.save(any(Rating.class))).thenReturn(ratingSaved);

		RatingDto resultRating = ratingService.createRating(ratingOneCategoryGlobalDto);

		assertEquals(ratingOneCategoryGlobalDto.getCategory().getId(), resultRating.getCategory().getId());
		assertEquals(ratingOneCategoryGlobalDto.getDaySheet().getId(), resultRating.getDaySheet().getId());
		assertEquals(ratingOneCategoryGlobalDto.getRating(), resultRating.getRating());
		assertEquals(ratingOneCategoryGlobalDto.getRatingRole(), resultRating.getRatingRole());
	}
}
