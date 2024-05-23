package ch.zhaw.pm4.compass.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ch.zhaw.pm4.compass.backend.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;

public interface DaySheetRepository extends JpaRepository<DaySheet, Long> {

	Optional<DaySheet> findById(Long id);

	Optional<DaySheet> findByIdAndOwnerId(Long id, String userId);

	Optional<DaySheet> findByDateAndOwnerId(LocalDate date, String userId);

	List<DaySheet> findAllByOwnerIdAndDateBetween(String userId, LocalDate firstMonthDay, LocalDate lastMonthDay);

	List<DaySheet> findAllByDate(LocalDate date);

	List<DaySheet> findAllByConfirmedIsFalseAndOwner_Role(UserRole role);
}
