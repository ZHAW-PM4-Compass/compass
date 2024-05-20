package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import ch.zhaw.pm4.compass.backend.model.LocalUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DaySheetDto {
	private Long id;
	private LocalDate date;
	private String day_notes;

	private Boolean confirmed = false;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "day_sheet_id" })
	private List<TimestampDto> timestamps;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "daysheet", "category.categoryOwners", "category.moodRatings" })
	private List<RatingDto> moodRatings;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "date", "user" })
	private List<IncidentDto> incidents;

	private long timeSum;

	private UserDto owner;

	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps,
					   List<RatingDto> moodRatings, List<IncidentDto> incidents, UserDto owner) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
		this.incidents = incidents;
		this.owner = owner;
		setTimeSum();
	}

	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps,
			List<RatingDto> moodRatings, List<IncidentDto> incidents) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
		this.incidents = incidents;
		setTimeSum();
	}

	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		setTimeSum();
	}

	public DaySheetDto(Long id, String day_notes, LocalDate date, List<RatingDto> moodRatings) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.moodRatings = moodRatings;
	}

	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
	}

	public DaySheetDto(String day_notes, LocalDate date, Boolean confirmed) {
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
	}

	private void setTimeSum() {
		for (TimestampDto timestamp : this.getTimestamps()) {
			LocalTime startTime = timestamp.getStart_time();
			LocalTime endTime = timestamp.getEnd_time();
			long durationInMilliseconds = Duration.between(startTime, endTime).toMillis();
			timeSum += durationInMilliseconds;
		}
	}
}
