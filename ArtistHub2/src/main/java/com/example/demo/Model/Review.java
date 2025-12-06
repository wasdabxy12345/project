package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long artistId;
	private Long customerId;
	private String artistName; // Which artist got the review
	private String customerName; // Who gave the review

	private String comment;
	private int rating; // Example: 1–5 stars
	private LocalDateTime date;
}
