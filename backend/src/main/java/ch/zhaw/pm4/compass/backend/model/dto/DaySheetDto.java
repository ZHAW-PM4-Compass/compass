package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DaySheetDto {

    private Long id;
    private LocalDate date;

    private String day_notes;

    private Boolean confirmed = false;
    private List<TimestampDto> timestamps;
    private long timeSum;

    public DaySheetDto() {

    }

    public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps) {
        this.id = id;
        this.date = date;
        this.day_notes = day_notes;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
        setTimeSum();
    }

    public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed) {
        this.id = id;
        this.date = date;
        this.day_notes = day_notes;
        this.confirmed = confirmed;
    }

    public DaySheetDto(String day_notes, LocalDate date, Boolean confirmed) {
        this.date = date;
        this.day_notes = day_notes;
        this.confirmed = confirmed;
    }

    private void setTimeSum() {
        for (TimestampDto timestamp : this.getTimestamps()) {
            timeSum += timestamp.getEnd_time().getTime() - timestamp.getStart_time().getTime();
        }
    }
}
