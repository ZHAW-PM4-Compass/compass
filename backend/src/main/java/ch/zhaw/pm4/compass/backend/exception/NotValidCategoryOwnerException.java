package ch.zhaw.pm4.compass.backend.exception;

public class NotValidCategoryOwnerException extends Exception {
	public NotValidCategoryOwnerException() {
		super("User found in list that cannot be a CategoryOwner");
	}

}
