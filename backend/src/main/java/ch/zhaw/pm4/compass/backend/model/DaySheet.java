package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class DaySheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Temporal(TemporalType.DATE)
    private LocalDate date;

    private String day_report;

    private Boolean confirmed = false;

    private String user_id;
    @OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public DaySheet() {

    }

    public DaySheet(String day_report, LocalDate date) {
        this.date = date;
        this.day_report = day_report;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id,String day_report, LocalDate date) {
        this.id = id;
        this.date = date;
        this.day_report = day_report;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id,String day_report, LocalDate date,Boolean confirmed) {
        this.id = id;
        this.date = date;
        this.day_report = day_report;
        this.confirmed = confirmed;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id, String day_report, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.day_report = day_report;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
    }
    public DaySheet(Long id,String user_id, String day_report, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.day_report = day_report;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
        this.user_id = user_id;
    }
    public DaySheet(LocalDate date) {
        this.date = date;
    }
}
