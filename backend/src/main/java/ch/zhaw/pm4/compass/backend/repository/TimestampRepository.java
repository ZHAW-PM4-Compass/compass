package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TimestampRepository  extends JpaRepository<Timestamp, Long>{
    Iterable<Timestamp> findAllByDaySheetId(Long daySheetId);
}
