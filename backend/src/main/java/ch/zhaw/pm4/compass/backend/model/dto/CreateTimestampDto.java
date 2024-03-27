package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
public class CreateTimestampDto {
    private Long day_sheet_id;

    private Time start_time;

    private Time end_time;


    public CreateTimestampDto() {
    }

    public CreateTimestampDto(Long day_sheet_id, Time start_time, Time end_time) {
        this.day_sheet_id = day_sheet_id;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}
