package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demo.Model.PerformanceFile;

public interface PerformanceFileRepository extends JpaRepository<PerformanceFile, Long> {
    List<PerformanceFile> findByArtistId(Long artistId);
}