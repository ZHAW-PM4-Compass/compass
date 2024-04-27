package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Category;

public class CategoryAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9071691353469205450L;

	public CategoryAlreadyExistsException(Category category) {
		super(String.format("Category with Name %s already exists", category.getName()));
	}
}
