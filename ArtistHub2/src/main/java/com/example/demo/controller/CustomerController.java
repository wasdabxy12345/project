package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Model.Customer;
import com.example.demo.repository.CustomerRepository;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerRepository customerRepo;

	public CustomerController(CustomerRepository customerRepo) {
		this.customerRepo = customerRepo;
	}

	// Show Registration Page
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute("customer", new Customer());
		return "register/customerRegister";
	}

	// Handle Form Submission
	@SuppressWarnings("null")
	@PostMapping("/register")
	public String registerCustomer(@ModelAttribute Customer customer) {
		customerRepo.save(customer); // Saves to DB
		return "redirect:/customer/login"; // Redirect after success
	}

	// Show login page
	@GetMapping("/login")
	public String showLoginPage(Model model) {
		model.addAttribute("customerLogin", new Customer());
		return "login/customerLogin";
	}

	// Handle login submission
	@PostMapping("/login")
	public String loginCustomer(@ModelAttribute("customerLogin") Customer customer, Model model) {
		Customer loggedCustomer = customerRepo.findByEmailAndPassword(customer.getEmail(), customer.getPassword());

		if (loggedCustomer != null)
			return "redirect:/customer/dashboard"; // Replace with actual dashboard later
		model.addAttribute("error", "Invalid email or password");
		return "login/customerLogin";
	}

	@GetMapping("/dashboard")
	public String customerDashboard() {
		return "dashboard/customerDashboard";
	}

}
