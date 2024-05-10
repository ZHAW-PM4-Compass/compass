package ch.zhaw.pm4.compass.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	private String dayNotes;

	private Boolean confirmed = false;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private LocalUser owner;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Timestamp> timestamps;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

	public DaySheet() {

	}

	public DaySheet(String dayNotes, LocalDate date) {
		this.date = date;
		this.dayNotes = dayNotes;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
	}

	public DaySheet(Long id, String dayNotes, LocalDate date) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
	}

	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
	}

	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = new ArrayList<>();
	}

	public DaySheet(Long id, LocalUser owner, String dayNotes, LocalDate date, Boolean confirmed,
			ArrayList<Timestamp> timestamps) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.owner = owner;
		this.moodRatings = new ArrayList<>();
	}

	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps,
			ArrayList<Rating> moodRatings) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
	}

	public DaySheet(Long id, LocalUser owner, String dayNotes, LocalDate date, Boolean confirmed,
			ArrayList<Timestamp> timestamps, ArrayList<Rating> moodRatings) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.owner = owner;
		this.moodRatings = moodRatings;
	}

	public DaySheet(LocalDate date) {
		this.date = date;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
	}
}
