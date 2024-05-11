package ch.zhaw.pm4.compass.backend.exception;

public class IncidentNotFoundException extends Exception {
	public IncidentNotFoundException(long id) {
		super(String.format("DaySheet with id %d not found", id));
	}
}
