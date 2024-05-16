package ch.zhaw.pm4.compass.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;

public interface DaySheetRepository extends JpaRepository<DaySheet, Long> {

	Optional<DaySheet> findByIdAndUserId(Long id, String userId);

	Optional<List<DaySheet>> findAllByUserId(String userId);

	List<DaySheet> findAllByUserIdAndDateBetween(String userId, LocalDate firstMonthDay, LocalDate lastMonthDay);

	Optional<DaySheet> findByDateAndUserId(LocalDate date, String userId);
}
