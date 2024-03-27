package ch.zhaw.pm4.compass.backend.exception;

public class TimestampNotFoundException extends RuntimeException {
    public TimestampNotFoundException(Long id) {
        super("Timestamp not found with id " + id);
    }
}