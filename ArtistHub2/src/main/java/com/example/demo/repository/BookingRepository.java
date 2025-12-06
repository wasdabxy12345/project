package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByArtistId(Long artistId);

    List<Booking> findByCustomerId(Long customerId);
}
