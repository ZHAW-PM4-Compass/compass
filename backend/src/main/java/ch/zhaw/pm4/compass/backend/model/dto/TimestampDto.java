package ch.zhaw.pm4.compass.backend.model.dto;

import java.sql.Time;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimestampDto {
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY)
	private Long id;

	private Long day_sheet_id;

	private Time start_time;

	private Time end_time;

	public boolean verifyTimeStamp() {
		return end_time.after(start_time);
	}
}
