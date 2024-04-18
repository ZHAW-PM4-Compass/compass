package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DaySheetRepository extends JpaRepository<DaySheet, Long>{

    Optional<DaySheet> getDaySheetById(Long authId);




    Optional<List<DaySheet>> getDaySheetByDate(LocalDate date);
}
