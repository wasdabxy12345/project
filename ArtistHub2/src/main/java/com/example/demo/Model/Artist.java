package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private String phone;
	private String password;
	private String category;
	private int xp;
	private int price;
	private String status = "PENDING"; // Default status

	public Artist() {
	}

	public Artist(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
}
