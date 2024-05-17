package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.zhaw.pm4.compass.backend.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
	@NonNull
	@JsonIgnoreProperties({ "categoryOwners", "moodRatings" })
	private CategoryDto category;
	@NonNull
	@JsonIgnore
	private DaySheetDto daySheet;
	@NonNull
	private Integer rating;
	@NonNull
	private RatingType ratingRole;
}
