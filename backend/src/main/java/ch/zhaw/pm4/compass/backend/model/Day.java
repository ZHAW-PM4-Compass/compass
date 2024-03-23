package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private String day_report;


    private Boolean confirmed = false;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public Day()
    {

    }
    public Day(String day_report, Date date)
    {
        this.date = date;
        this.day_report = day_report;
    }
    public Day(Date date)
    {
        this.date = date;
    }
}
