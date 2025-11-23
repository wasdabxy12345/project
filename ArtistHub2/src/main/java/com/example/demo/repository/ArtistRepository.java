package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
	Artist findByEmailAndPassword(String email, String password);

	Artist findByEmail(String name);
}
