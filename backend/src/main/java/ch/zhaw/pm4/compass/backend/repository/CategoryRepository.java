package ch.zhaw.pm4.compass.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.LocalUser;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);

	Optional<Category> findByName(String name);

	@Query(value = "SELECT c FROM Category c WHERE c.categoryOwners IS EMPTY")
	Iterable<Category> findGlobalCategories();

	Iterable<Category> findAllByCategoryOwners(LocalUser categoryOwner);
}
