package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateDayDto {


    private Date date;

    private String day_report;


    public CreateDayDto()
    {

    }
    public CreateDayDto(String day_report, Date date)
    {
        this.date = date;
        this.day_report = day_report;
    }
    public CreateDayDto(Date date)
    {
        this.date = date;
    }
}
