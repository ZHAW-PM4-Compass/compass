package ch.zhaw.pm4.compass.backend.exception;

public class GlobalCategoryException extends Exception {
	public GlobalCategoryException() {
		super("Cannot link Users to a global Category");
	}
}