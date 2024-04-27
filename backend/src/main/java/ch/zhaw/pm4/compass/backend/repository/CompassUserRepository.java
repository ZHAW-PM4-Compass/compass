package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.CompassUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompassUserRepository extends JpaRepository<CompassUser, String> {
}
