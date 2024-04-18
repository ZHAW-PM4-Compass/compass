package ch.zhaw.pm4.compass.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;


@Getter
@Setter
@Entity
public class Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_id;
    private Time startTime;
    private Time endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daySheet_id")
    @JsonIgnore
    private DaySheet daySheet;

    public Timestamp() {

    }

    public Timestamp(DaySheet daySheet, Time startTime, Time endTime) {
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Timestamp(Long id, DaySheet daySheet, Time startTime, Time endTime) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Timestamp(Long id, DaySheet daySheet, Time startTime, Time endTime,String user_id) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user_id = user_id;
    }
}
