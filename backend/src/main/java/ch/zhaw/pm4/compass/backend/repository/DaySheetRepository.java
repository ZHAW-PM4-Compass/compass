package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;


public interface DaySheetRepository extends JpaRepository<DaySheet, Long>{

    Optional<DaySheet> getDaySheetById(Long authId);

    Optional<DaySheet> getDaySheetByDate(Date day);
}
