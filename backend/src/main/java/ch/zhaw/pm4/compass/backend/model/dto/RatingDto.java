package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.zhaw.pm4.compass.backend.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) for representing ratings within the system. Each
 * rating is linked to a specific category and day sheet and includes additional
 * attributes such as the rating value and the role of the rating.
 *
 * Lombok annotations are used to simplify the creation of getters and
 * constructors
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas,
 *         zimmenoe
 * @version 26.05.2024
 */
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
