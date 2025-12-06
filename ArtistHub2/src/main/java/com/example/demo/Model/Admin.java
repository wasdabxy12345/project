package com.example.demo.model;

import lombok.Data;

@Data
public class Admin {

	private String username;
	private String password;

	// constructors
	public Admin() {
	}

	public Admin(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
