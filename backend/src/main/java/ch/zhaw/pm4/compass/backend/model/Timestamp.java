package ch.zhaw.pm4.compass.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalTime;


@Getter
@Setter
@Entity
public class Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daySheet_id")
    @JsonIgnore
    private DaySheet daySheet;

    public Timestamp() {

    }

    public Timestamp(DaySheet daySheet, LocalTime startTime, LocalTime endTime) {
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timestamp(Long id, DaySheet daySheet, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timestamp(Long id, DaySheet daySheet, LocalTime startTime, LocalTime endTime, String userId) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
    }
}
