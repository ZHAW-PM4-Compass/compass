package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDaySheetDayNotesDto {

    private Long id;

    private String day_notes;

    public UpdateDaySheetDayNotesDto() {

    }

    public UpdateDaySheetDayNotesDto(Long dayId, String day_notes) {
        this.id = dayId;
        this.day_notes = day_notes;
    }


}
