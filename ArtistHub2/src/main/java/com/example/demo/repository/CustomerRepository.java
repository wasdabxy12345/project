package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Customer findByEmailAndPassword(String email, String password);

}
