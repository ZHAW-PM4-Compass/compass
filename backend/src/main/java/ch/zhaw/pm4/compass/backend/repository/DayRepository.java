package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DayRepository  extends JpaRepository<Day, Long>{

    Optional<Day> getDayById(Long authId);
}
