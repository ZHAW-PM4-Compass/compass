package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkHourDto {

    private Long daySheetId;

    private LocalDate date;

    private Boolean confirmed = false;

    private Long workHours;

    private ParticipantDto participant;

    public WorkHourDto() {

    }

    public WorkHourDto(Long daySheetId, LocalDate date, Boolean confirmed, Long workHours, ParticipantDto participant) {
        this.daySheetId = daySheetId;
        this.date = date;
        this.confirmed = confirmed;
        this.workHours = workHours;
        this. participant = participant;
    }
}
