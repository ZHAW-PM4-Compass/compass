package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface DaySheetRepository extends JpaRepository<DaySheet, Long>{

    Optional<DaySheet> findByIdAndUserId(Long id, String userId);




    Optional<DaySheet> findByDateAndUserId(LocalDate date, String userId);
}
