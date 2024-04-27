package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.Rating;

public class RatingIsNotValidException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2064461522110054964L;

	public RatingIsNotValidException(Rating rating, Category category) {
		super(String.format("Rating is not within range 0f %s: Min(%d) - Max:(%d)", category.getName(),
				category.getMinimumValue(), category.getMaximumValue()));
	}
}
