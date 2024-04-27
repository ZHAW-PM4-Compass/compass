package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaySheetDto {

	private Long id;

	private LocalDate date;

	private String day_report;

	private Boolean confirmed = false;

	private List<TimestampDto> timestamps;

	public DaySheetDto() {

	}

	public DaySheetDto(Long id, String day_report, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps) {
		this.id = id;
		this.date = date;
		this.day_report = day_report;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
	}

	public DaySheetDto(Long id, String day_report, LocalDate date, Boolean confirmed) {
		this.id = id;
		this.date = date;
		this.day_report = day_report;
		this.confirmed = confirmed;
	}

	public DaySheetDto(String day_report, LocalDate date, Boolean confirmed) {
		this.date = date;
		this.day_report = day_report;
		this.confirmed = confirmed;
	}
}
