package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDto {
	private Long id;
	private String title;
	private String description;
	private LocalDate date;
	private	String userId;
	private String userEmail;
}
