package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DaySheetDto {
	private Long id;

	private LocalDate date;

	private String day_report;

	private Boolean confirmed = false;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private List<TimestampDto> timestamps;

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
