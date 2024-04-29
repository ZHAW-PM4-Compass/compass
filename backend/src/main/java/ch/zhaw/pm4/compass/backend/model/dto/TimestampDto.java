package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class TimestampDto {

    private Long id;
    private Long day_sheet_id;

    private Time start_time;

    private Time end_time;


    public TimestampDto() {
    }

    public TimestampDto(Long id, Long day_sheet_id, Time start_time, Time end_time) {
        this.id = id;
        this.day_sheet_id = day_sheet_id;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}
