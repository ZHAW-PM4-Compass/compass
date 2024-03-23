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

    private Time startTime;
    private Time endTime;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="day_id")
    @JsonIgnore
    private Day day;

    public Timestamp()
    {

    }

}
