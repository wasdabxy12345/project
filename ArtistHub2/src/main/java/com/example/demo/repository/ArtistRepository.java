package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.model.Artist;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
	Artist findByEmailAndPassword(String email, String password);
	Artist findByEmail(String name);
    Artist findByCategory(String category);
	@Query("SELECT DISTINCT a.category FROM Artist a")
	List<String> findDistinctCategories();
}
