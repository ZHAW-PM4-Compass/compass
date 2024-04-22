package ch.zhaw.pm4.compass.backend.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class CategoryDto {
	@NonNull
	private String name;
	@NonNull
	private Integer minimumValue;
	@NonNull
	private Integer maximumValue;

	private List<RatingDto> moodRatings;
}
