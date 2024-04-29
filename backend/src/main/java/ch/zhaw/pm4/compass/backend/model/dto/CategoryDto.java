package ch.zhaw.pm4.compass.backend.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	private List<ParticipantDto> categoryOwners;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private List<RatingDto> moodRatings;

	public CategoryDto(Long id, String name, Integer minimumValue, Integer maximumValue,
			List<ParticipantDto> categoryOwners) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
		this.categoryOwners = categoryOwners;
	}

	public CategoryDto(@JsonProperty("name") String name, @JsonProperty("minimumValue") Integer minimumValue,
			@JsonProperty("maximumValue") Integer maximumValue) {
		this.name = name;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
		this.categoryOwners = new ArrayList<>();
	}
}
