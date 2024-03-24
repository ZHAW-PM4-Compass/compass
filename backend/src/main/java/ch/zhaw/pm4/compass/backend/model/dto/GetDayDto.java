package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GetDayDto {

    private Long id;

    private Date date;

    private String day_report;


    private Boolean confirmed = false;

    private List<Timestamp> timestamps;

    public GetDayDto()
    {

    }
    public GetDayDto(String day_report, Date date,Boolean confirmed,List<Timestamp> timestamps)
    {
        this.date = date;
        this.day_report = day_report;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
    }
}
