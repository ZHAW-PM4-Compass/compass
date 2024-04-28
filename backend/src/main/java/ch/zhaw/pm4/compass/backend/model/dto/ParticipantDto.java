package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipantDto {
	@NonNull
	private String id;
}
