package ch.zhaw.pm4.compass.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);

	Optional<Category> findByName(String name);
}
