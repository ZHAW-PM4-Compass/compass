package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Incident {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private DaySheet daySheet;

	public Incident(String title, String description) {
		this.title = title;
		this.description = description;
	}
}
