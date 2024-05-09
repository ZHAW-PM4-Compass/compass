package ch.zhaw.pm4.compass.backend.model;

import java.util.List;

import ch.zhaw.pm4.compass.backend.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

	@ManyToMany(mappedBy = "categoryOwners")
	private List<Category> categories;

	public LocalUser(String id, UserRole role) {
		this.id = id;
		this.role = role;
	}
    public boolean isEmpty() {
        return this.id.isEmpty();
    }
}