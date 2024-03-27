package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class DaySheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String day_report;

    private Boolean confirmed = false;

    @OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public DaySheet() {

    }

    public DaySheet(String day_report, Date date) {
        this.date = date;
        this.day_report = day_report;
    }

    public DaySheet(Date date) {
        this.date = date;
    }
}
