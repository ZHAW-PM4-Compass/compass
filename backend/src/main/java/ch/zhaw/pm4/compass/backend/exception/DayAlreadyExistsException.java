package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Day;

public class DayAlreadyExistsException extends RuntimeException {
    public DayAlreadyExistsException(Day day) {
        super("Day with date " + day.getDate().toString() + "Alreadey exists");
    }
}