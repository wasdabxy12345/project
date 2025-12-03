package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Model.Artist;
import com.example.demo.Model.Customer;
import com.example.demo.Model.PerformanceFile;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PerformanceFileRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private final CustomerRepository customerRepo;
	@Autowired
    private final ArtistRepository artistRepo;
	@Autowired
	private final PerformanceFileRepository performanceFileRepo;

	public CustomerController(CustomerRepository customerRepo, ArtistRepository artistRepo, PerformanceFileRepository performanceFileRepo) {
		this.customerRepo = customerRepo;
		this.artistRepo = artistRepo;
		this.performanceFileRepo = performanceFileRepo;
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
	public String loginCustomer(@ModelAttribute("customerLogin") Customer customer, Model model, HttpSession session) {
		Customer loggedCustomer = customerRepo.findByEmailAndPassword(customer.getEmail(), customer.getPassword());

		if (loggedCustomer != null){
			session.setAttribute("loggedCustomer", loggedCustomer);
			return "redirect:/customer/dashboard";
		}
		model.addAttribute("error", "Invalid email or password");
		return "login/customerLogin";
	}

	@GetMapping("/dashboard")
	public String customerDashboard(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute("loggedCustomer");
		if (customer == null)
			return "redirect:/customer/login";
		model.addAttribute("customer", customer);
		return "dashboard/customerDashboard";
	}

	// method to manage profile
	@GetMapping("/manageProfile")
	public String manageProfile(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute("loggedCustomer");
		if (customer == null)
			return "redirect:/customer/login";
		model.addAttribute("customer", customer);
		return "customer/manageProfile";
	}

	@PostMapping("/manageProfile")
	public String updateProfile(HttpSession session, @ModelAttribute Customer updatedCustomer) {
		Customer customer = (Customer) session.getAttribute("loggedCustomer");
		if (customer == null)
			return "redirect:/customer/login";

		customer.setName(updatedCustomer.getName());
		customer.setEmail(updatedCustomer.getEmail());
		customer.setPassword(updatedCustomer.getPassword());
		customerRepo.save(customer);
		return "redirect:/customer/manageProfile";
	}

	// methtod to view artists details and search by category
	@GetMapping("/searchArtist")
	public String searchArtist(HttpSession session, Model model, String category) {
		Customer customer = (Customer) session.getAttribute("loggedCustomer");
		if (customer == null)
			return "redirect:/customer/login";
		
		if (category != null && !category.isEmpty()) {
			model.addAttribute("artists", artistRepo.findByCategory(category));
		} else {
			model.addAttribute("artists", artistRepo.findAll());
		}
		return "customer/searchArtist";
	}

	// method to view artist details and portfolio
	@GetMapping("/viewArtist/{id}")
	public String viewArtist(HttpSession session, Model model, @PathVariable Long id) {
		Customer customer = (Customer) session.getAttribute("loggedCustomer");
		if (customer == null)
			return "redirect:/customer/login";
		
		Artist artist = artistRepo.findById(id).orElse(null);
		if (artist == null)
			return "redirect:/customer/searchArtist";
		
		List<PerformanceFile> portfolio = performanceFileRepo.findByArtistId(id);
		
		model.addAttribute("artist", artist);
		model.addAttribute("portfolio", portfolio);
		return "customer/viewArtist";
	}
}
