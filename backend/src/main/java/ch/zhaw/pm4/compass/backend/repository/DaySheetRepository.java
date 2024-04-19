package ch.zhaw.pm4.compass.backend.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;

public interface DaySheetRepository extends JpaRepository<DaySheet, Long> {
	Optional<DaySheet> getDaySheetById(Long authId);

	Optional<DaySheet> getDaySheetByDate(Date day);
}
