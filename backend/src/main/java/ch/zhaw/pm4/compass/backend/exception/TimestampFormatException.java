package ch.zhaw.pm4.compass.backend.exception;

public class TimestampFormatException extends RuntimeException {
    public TimestampFormatException() {
        super("Wrong Timestamp format");
    }
}