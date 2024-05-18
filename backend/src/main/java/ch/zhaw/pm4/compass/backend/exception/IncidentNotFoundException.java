package ch.zhaw.pm4.compass.backend.exception;

public class IncidentNotFoundException extends Exception {
	private static final long serialVersionUID = -2803300336215487208L;

	public IncidentNotFoundException(long id) {
		super(String.format("DaySheet with id %d not found", id));
	}
}
