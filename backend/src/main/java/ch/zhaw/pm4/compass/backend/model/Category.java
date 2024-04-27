package ch.zhaw.pm4.compass.backend.model;

import java.util.List;

import org.springframework.data.annotation.PersistenceCreator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;

	private Integer minimumValue;
	private Integer maximumValue;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

	@PersistenceCreator
	public Category(String name, int minimumValue, int maximumValue) {
		this.name = name;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}

	public boolean isValidRating(Rating rating) {
		return rating.getRating() >= this.minimumValue && rating.getRating() <= this.maximumValue;
	}
}
