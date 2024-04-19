package ch.zhaw.pm4.compass.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
	Iterable<Rating> findAllByDaySheetId(Long daySheetId);

	Iterable<Rating> findAllByCategoryId(Long categoryId);
}
