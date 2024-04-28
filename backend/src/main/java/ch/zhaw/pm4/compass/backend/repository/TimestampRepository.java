package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    Iterable<Timestamp> findAllByDaySheetIdAndUserId(Long daySheetId, String userId);

    Optional<Timestamp> findByIdAndUserId(Long id, String userId);


}
