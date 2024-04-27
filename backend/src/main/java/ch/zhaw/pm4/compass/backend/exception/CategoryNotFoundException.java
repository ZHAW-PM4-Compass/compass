package ch.zhaw.pm4.compass.backend.exception;

public class CategoryNotFoundException extends Exception {
	private static final long serialVersionUID = 5766981433329948942L;

	public CategoryNotFoundException(long id) {
		super(String.format("Category with id %d not found", id));
	}
}
