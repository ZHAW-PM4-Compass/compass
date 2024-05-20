package ch.zhaw.pm4.compass.backend.model;

import java.time.LocalTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timestamp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daySheet_id")
    private DaySheet daySheet;

    public Timestamp(LocalTime startTime, LocalTime endTime, DaySheet daySheet) {
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timestamp(Long id, LocalTime startTime, LocalTime endTime, DaySheet daySheet) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timestamp(Long id, LocalTime startTime, LocalTime endTime, DaySheet daySheet, String userId) {
        this.id = id;
        this.daySheet = daySheet;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
    }
}
