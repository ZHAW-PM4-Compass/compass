package ch.zhaw.pm4.compass.backend.model;

import ch.zhaw.pm4.compass.backend.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LocalUser {
	@Id
	private String id;

	@Enumerated(EnumType.STRING)
	private UserRole role;
}
