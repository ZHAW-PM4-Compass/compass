package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalTime;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimestampDto {
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY)
	private Long id;

	private Long day_sheet_id;
	@Schema(type = "string", example = "10:00:00")
	private LocalTime start_time;
	@Schema(type = "string", example = "10:00:00")
	private LocalTime end_time;

	public boolean verifyTimeStamp() {
		return end_time.isAfter(start_time);
	}
}
