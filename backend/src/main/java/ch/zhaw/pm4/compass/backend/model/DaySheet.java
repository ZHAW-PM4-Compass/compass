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

    private String dayNotes;

    private Boolean confirmed = false;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "user_id", referencedColumnName = "user_id", table = "local_user", nullable = false)
    private String userId;
    @OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
    private List<Timestamp> timestamps;

    public DaySheet() {

    }

    public DaySheet(String dayNotes, LocalDate date) {
        this.date = date;
        this.dayNotes = dayNotes;
        this.timestamps = new ArrayList<>();
    }

    public DaySheet(Long id, String dayNotes, LocalDate date) {
        this.id = id;
        this.date = date;
        this.dayNotes = dayNotes;
        this.timestamps = new ArrayList<>();
    }

    public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed) {
        this.id = id;
        this.date = date;
        this.dayNotes = dayNotes;
        this.confirmed = confirmed;
        this.timestamps = new ArrayList<>();
    }

    public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.dayNotes = dayNotes;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
    }

    public DaySheet(Long id, String userId, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
        this.id = id;
        this.date = date;
        this.dayNotes = dayNotes;
        this.confirmed = confirmed;
        this.timestamps = timestamps;
        this.userId = userId;
    }

    public DaySheet(LocalDate date) {
        this.date = date;
    }
}
