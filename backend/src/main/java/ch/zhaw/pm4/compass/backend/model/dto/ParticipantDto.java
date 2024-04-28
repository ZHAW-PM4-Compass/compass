package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class ParticipantDto {
    private Long id;
    private String name;


    public ParticipantDto() {
    }

    public ParticipantDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
