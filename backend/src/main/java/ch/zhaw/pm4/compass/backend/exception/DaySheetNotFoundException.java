package ch.zhaw.pm4.compass.backend.exception;

public class DaySheetNotFoundException extends Exception {
	private static final long serialVersionUID = -7905280285542310018L;

	public DaySheetNotFoundException(long id) {
		super(String.format("DaySheet with id %d not found", id));
	}
}
