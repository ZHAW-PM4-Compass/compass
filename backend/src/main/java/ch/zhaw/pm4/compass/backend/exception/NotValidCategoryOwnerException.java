package ch.zhaw.pm4.compass.backend.exception;

public class NotValidCategoryOwnerException extends Exception {
	private static final long serialVersionUID = 6583230349633757980L;

	public NotValidCategoryOwnerException() {
		super("User found in list that cannot be a CategoryOwner");
	}

}
