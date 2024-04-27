package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;

public class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private long categoryId = 1l;
	private String categoryName = "Stress";
	private int minValue = 1;
	private int maxValue = 10;

	private CategoryDto getCategoryDto() {
		return new CategoryDto(categoryId, categoryName, minValue, maxValue);
	}

	private Category getCategory() {
		Category category = new Category(categoryName, minValue, maxValue);
		category.setId(categoryId);
		return category;
	}

	@Test
	public void whenCreatingCategory_expectSameCategoryToBeReturned() throws Exception {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		CategoryDto resultCategory = categoryService.createCategory(createCategory);
		assertEquals(createCategory, resultCategory);
	}

	@Test()
	public void whenCreatingCategoryThatExists_expectException() {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
		assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(createCategory));
	}

	@Test()
	public void whenGettingCategoryByName_expectCorrectCategory() {
		Category category = getCategory();
		CategoryDto createCategory = getCategoryDto();
		when(categoryRepository.findByName(createCategory.getName())).thenReturn(Optional.of(category));
		CategoryDto resultCategory = categoryService.getCategoryByName(categoryName, false);
		assertEquals(createCategory, resultCategory);
	}

	@Test
	public void whenGettingNonExistantCategoryName_expectException() {
		when(categoryRepository.findByName(any())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryByName(categoryName, false));
	}
}
