package ch.zhaw.pm4.compass.backend.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) for categories used within the Compass application.
 * This DTO includes details about category identifiers, names, value ranges, owners, and associated ratings.
 * Utilizes Lombok for simplifying the codebase by automatically generating getters, setters, equals, hashCode, and toString methods.
 *
 * @version 26.05.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Details about the category")
public class CategoryDto {
    @NonNull
    @ApiModelProperty(notes = "The unique identifier of the category")
    private Long id;

    @ApiModelProperty(notes = "The name of the category")
    private String name;

    @ApiModelProperty(notes = "The minimum value for ratings within this category")
    private Integer minimumValue;

    @ApiModelProperty(notes = "The maximum value for ratings within this category")
    private Integer maximumValue;

    @ApiModelProperty(notes = "The list of participants who own this category")
    private List<ParticipantDto> categoryOwners = new ArrayList<>();

    @ApiModelProperty(notes = "The list of mood ratings associated with this category")
    private List<RatingDto> moodRatings = new ArrayList<>();

    /**
     * Constructs a CategoryDto with all fields except mood ratings.
     *
     * @param id The unique identifier of the category.
     * @param name The name of the category.
     * @param minimumValue The minimum value for ratings within this category.
     * @param maximumValue The maximum value for ratings within this category.
     * @param categoryOwners The list of participants who own this category.
     */
    public CategoryDto(Long id, String name, Integer minimumValue, Integer maximumValue,
                       List<ParticipantDto> categoryOwners) {
        this.id = id;
        this.name = name;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.categoryOwners = categoryOwners;
    }

    /**
     * Constructs a CategoryDto using JSON properties, initializing the list of category owners.
     * This constructor is used for JSON deserialization.
     *
     * @param id The unique identifier of the category.
     * @param name The name of the category.
     * @param minimumValue The minimum value for ratings within this category.
     * @param maximumValue The maximum value for ratings within this category.
     */
    public CategoryDto(@JsonProperty("id") Long id, @JsonProperty("name") String name,
                       @JsonProperty("minimumValue") Integer minimumValue, @JsonProperty("maximumValue") Integer maximumValue) {
        this.id = id;
        this.name = name;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.categoryOwners = new ArrayList<>();
        this.moodRatings = new ArrayList<>();
    }
}
