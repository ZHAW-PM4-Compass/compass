package ch.zhaw.pm4.compass.backend.model;

import java.util.Date;
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
	private Date date;

	private String day_report;

	private Boolean confirmed = false;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Timestamp> timestamps;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

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
