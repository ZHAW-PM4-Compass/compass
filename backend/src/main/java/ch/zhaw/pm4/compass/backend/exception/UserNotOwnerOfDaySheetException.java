package ch.zhaw.pm4.compass.backend.exception;

public class UserNotOwnerOfDaySheetException extends Exception {
	private static final long serialVersionUID = 8883786356323374443L;

	public UserNotOwnerOfDaySheetException() {
		super("User is not owner of Daysheet to be edited");
	}

}
