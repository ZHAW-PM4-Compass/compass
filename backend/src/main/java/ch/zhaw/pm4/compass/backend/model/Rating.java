package ch.zhaw.pm4.compass.backend.model;

import org.springframework.data.annotation.PersistenceCreator;

import ch.zhaw.pm4.compass.backend.RatingType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int rating;

	@Enumerated(EnumType.STRING)
	private RatingType ratingRole;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Category category;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private DaySheet daySheet;

	@PersistenceCreator
	public Rating(int rating, RatingType ratingRole, Category category, DaySheet daySheet) {
		this.rating = rating;
		this.ratingRole = ratingRole;
		this.category = category;
		this.daySheet = daySheet;
	}
}
