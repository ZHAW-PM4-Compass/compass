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

    private String dayReport;

    private Boolean confirmed = false;

    private String userId;
    @OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public DaySheet() {

    }

    public DaySheet(String dayReport, LocalDate date) {
        this.date = date;
        this.dayReport = dayReport;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id, String dayReport, LocalDate date) {
        this.id = id;
        this.date = date;
        this.dayReport = dayReport;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id, String dayReport, LocalDate date, Boolean confirmed) {
        this.id = id;
        this.date = date;
        this.dayReport = dayReport;
        this.confirmed = confirmed;
        this.timestamps = new ArrayList<>();
    }
    public DaySheet(Long id, String dayReport, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.dayReport = dayReport;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
    }
    public DaySheet(Long id, String userId, String dayReport, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.dayReport = dayReport;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
        this.userId = userId;
    }
    public DaySheet(LocalDate date) {
        this.date = date;
    }
}
