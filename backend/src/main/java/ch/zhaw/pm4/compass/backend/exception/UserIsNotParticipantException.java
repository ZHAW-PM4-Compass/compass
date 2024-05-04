package ch.zhaw.pm4.compass.backend.exception;

public class UserIsNotParticipantException extends Exception {
	public UserIsNotParticipantException() {
		super("User is not a Participant");
	}
}
