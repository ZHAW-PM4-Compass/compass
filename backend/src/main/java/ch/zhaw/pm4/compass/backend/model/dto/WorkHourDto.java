package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class WorkHourDto {

    private Long daySheetId;

    private Date date;

    private Boolean confirmed = false;

    private Long workHours;

    private ParticipantDto participant;

    public WorkHourDto() {

    }

    public WorkHourDto(Long daySheetId, Date date, Boolean confirmed, Long workHours, ParticipantDto participant) {
        this.daySheetId = daySheetId;
        this.date = date;
        this.confirmed = confirmed;
        this.workHours = workHours;
        this. participant = participant;
    }
}
