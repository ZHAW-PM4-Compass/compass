package ch.zhaw.pm4.compass.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DaySheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.DATE)
	private LocalDate date;

	private String dayReport;

	private Boolean confirmed = false;

	private String userId;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Timestamp> timestamps;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

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

	public DaySheet(Long id, String userId, String dayReport, LocalDate date, Boolean confirmed,
			ArrayList<Timestamp> timestamps) {
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
