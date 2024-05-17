package ch.zhaw.pm4.compass.backend.exception;

public class GlobalCategoryException extends Exception {
	private static final long serialVersionUID = 5529127806103716409L;

	public GlobalCategoryException() {
		super("Cannot link Users to a global Category");
	}
}