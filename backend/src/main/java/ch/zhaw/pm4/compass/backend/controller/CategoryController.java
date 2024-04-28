package ch.zhaw.pm4.compass.backend.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category Controller", description = "Category Endpoint")
@RestController
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@PostMapping(produces = "application/json")
	public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto category) {
		try {
			return ResponseEntity.ok(categoryService.createCategory(category));
		} catch (CategoryAlreadyExistsException | NotValidCategoryOwnerException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/getByName/{name}", produces = "application/json")
	public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
		try {
			return ResponseEntity.ok(categoryService.getCategoryByName(name, false));
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/getByNameWithAllRatings/{name}", produces = "application/json")
	public ResponseEntity<CategoryDto> getCategoryByNameWithRatings(@PathVariable String name) {
		try {
			return ResponseEntity.ok(categoryService.getCategoryByName(name, true));
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
