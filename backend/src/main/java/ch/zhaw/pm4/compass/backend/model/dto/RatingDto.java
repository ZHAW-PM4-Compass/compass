package ch.zhaw.pm4.compass.backend.model.dto;

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
	private CategoryDto category;
	@NonNull
	private DaySheetDto daySheet;
	@NonNull
	private Integer rating;
	@NonNull
	private RatingType ratingRole;
}
