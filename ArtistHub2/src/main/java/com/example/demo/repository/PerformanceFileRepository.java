package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PerformanceFile;

import java.util.List;

public interface PerformanceFileRepository extends JpaRepository<PerformanceFile, Long> {
    List<PerformanceFile> findByArtistId(Long artistId);
}