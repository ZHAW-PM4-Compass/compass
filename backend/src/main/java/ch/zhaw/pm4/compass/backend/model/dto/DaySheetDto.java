package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

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
	private List<TimestampDto> timestamps;

	private long timeSum;

	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		setTimeSum();
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
			timeSum += timestamp.getEnd_time().getTime() - timestamp.getStart_time().getTime();
		}
	}
}
