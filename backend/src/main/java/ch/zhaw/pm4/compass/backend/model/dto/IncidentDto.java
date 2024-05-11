package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDto {
	private Long id;
	@NonNull
	@JsonIgnore
	private DaySheetDto daySheet;
	private String title;
	private String description;
}
