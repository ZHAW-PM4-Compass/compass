package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.DaySheet;

public class DayAlreadyExistsException extends RuntimeException {
    public DayAlreadyExistsException(DaySheet daySheet) {
        super("DaySheet with date " + daySheet.getDate().toString() + "Alreadey exists");
    }
}