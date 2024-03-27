package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateDaySheetDto {

    private Long id;
    private Date date;
    private String day_report;


    public UpdateDaySheetDto() {
    }

    public UpdateDaySheetDto(Long id, String day_report, Date date) {
        this.id = id;
        this.date = date;
        this.day_report = day_report;
    }
}
