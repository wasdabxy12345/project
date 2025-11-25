package com.example.demo.Model;

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
