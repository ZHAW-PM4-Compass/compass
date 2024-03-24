package ch.zhaw.pm4.compass.backend.exception;

import java.util.Date;

public class DayNotFoundException extends RuntimeException {
    public DayNotFoundException(Long userId) {
        super("Day not found with id " + userId);
    }

    public DayNotFoundException(Date date) {
        super("Day not found with id " + date.toString());
    }
}