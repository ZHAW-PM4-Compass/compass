package ch.zhaw.pm4.compass.backend.model;

import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Timestamp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;
	private Time startTime;
	private Time endTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "daySheet_id")
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

	public Timestamp(Long id, DaySheet daySheet, Time startTime, Time endTime, String userId) {
		this.id = id;
		this.daySheet = daySheet;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userId = userId;
	}
}
