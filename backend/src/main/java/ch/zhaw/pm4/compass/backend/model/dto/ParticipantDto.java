package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ParticipantDto {
	@NonNull
	private Long id;
	@NonNull
	private String name;
}
