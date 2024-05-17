package ch.zhaw.pm4.compass.backend.exception;

public class TooManyRatingsPerCategoryException extends Exception {
	private static final long serialVersionUID = 5840233382938998438L;

	public TooManyRatingsPerCategoryException() {
		super("Only one rating per Category is allowed");
	}
}
