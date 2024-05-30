package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.zhaw.pm4.compass.backend.RatingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) for representing ratings within the system.
 * Each rating is linked to a specific category and day sheet and includes additional attributes
 * such as the rating value and the role of the rating.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors.
 *
 * @version 26.05.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Details about the rating")
public class RatingDto {
    @NonNull
    @ApiModelProperty(notes = "The category associated with this rating")
    private CategoryDto category;

    @NonNull
    @JsonIgnore
    @ApiModelProperty(notes = "The day sheet associated with this rating", hidden = true)
    private DaySheetDto daySheet;

    @NonNull
    @ApiModelProperty(notes = "The value of the rating")
    private Integer rating;

    @NonNull
    @ApiModelProperty(notes = "The role of the rating")
    private RatingType ratingRole;
}
