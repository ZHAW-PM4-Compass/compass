package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDayDto {

    private Long id;

    private String day_report;


    public UpdateDayDto()
    {

    }
    public UpdateDayDto(Long id,String day_report)
    {
        this.id = id;
        this.day_report = day_report;
    }

}
