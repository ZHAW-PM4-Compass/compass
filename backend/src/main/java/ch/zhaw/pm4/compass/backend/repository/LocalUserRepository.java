package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocalUserRepository extends JpaRepository<LocalUser, String> {
}
