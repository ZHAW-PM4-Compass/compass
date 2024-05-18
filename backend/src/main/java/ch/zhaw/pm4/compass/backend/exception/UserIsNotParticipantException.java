package ch.zhaw.pm4.compass.backend.exception;

public class UserIsNotParticipantException extends Exception {
	private static final long serialVersionUID = 7138122560782561252L;

	public UserIsNotParticipantException() {
		super("User is not a Participant");
	}
}
