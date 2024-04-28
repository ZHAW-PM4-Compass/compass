package ch.zhaw.pm4.compass.backend.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
	@NonNull
	private Long id;

	private String name;

	private Integer minimumValue;

	private Integer maximumValue;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private List<RatingDto> moodRatings;

	public CategoryDto(Long id, String name, Integer minimumValue, Integer maximumValue) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}

}