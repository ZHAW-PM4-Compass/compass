package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LocalUser {
    @Id
    private String id;
    private String role;

    public LocalUser() {

    }

    public LocalUser(String id, String role) {
        this.id = id;
        this.role = role;
    }

    public boolean isEmpty() {
        return this.id.isEmpty() && this.role.isEmpty();
    }
}