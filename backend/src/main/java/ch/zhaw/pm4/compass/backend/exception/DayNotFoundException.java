package ch.zhaw.pm4.compass.backend.exception;

public class DayNotFoundException extends RuntimeException {
    public DayNotFoundException(Long userId) {
        super("Day not found with id " + userId);
    }
}