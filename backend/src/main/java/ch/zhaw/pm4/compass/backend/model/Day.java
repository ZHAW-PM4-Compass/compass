package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private String day_report;
    public Day()
    {

    }

    public Day(String day_report, Date date)
    {
        this.date = date;
        this.day_report = day_report;
    }
}
