package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class CompassUser {
    @Id
    private String id;

    private String role;

    public CompassUser() {

    }

    public CompassUser(String id, String role) {
        this.id = id;
        this.role = role;
    }

}
