package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Category;

public class CategoryAlreadyExistsException extends Exception {
	public CategoryAlreadyExistsException(Category category) {
		super("Category with Name " + category.getName() + " already exists");
	}
}
