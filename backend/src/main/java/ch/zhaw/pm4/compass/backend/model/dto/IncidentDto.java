package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDto {
	private Long id;
	private String title;
	private String description;
	private Date date;
	private String userEmail;
	@NonNull
	@JsonIgnore
	private DaySheetDto daySheet;
	@NonNull
	@JsonIgnore
	private UserDto user;
}
