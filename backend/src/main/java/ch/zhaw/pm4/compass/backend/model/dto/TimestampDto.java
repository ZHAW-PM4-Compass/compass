package ch.zhaw.pm4.compass.backend.model.dto;

import java.sql.Time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimestampDto {

	private Long id;
	private Long day_sheet_id;

	private Time start_time;

	private Time end_time;

	public boolean verifyTimeStamp() {
		return end_time.after(start_time);
	}
}
