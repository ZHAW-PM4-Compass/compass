package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    Iterable<Timestamp> findAllByDaySheetId(Long daySheetId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM timestamp b WHERE b.id=:id",
            nativeQuery = true)
    void deleteTimestamp(@Param("id") Long id);
}
