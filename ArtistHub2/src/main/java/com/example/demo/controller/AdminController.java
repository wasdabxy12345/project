package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Model.Artist;
import com.example.demo.Model.Booking;
import com.example.demo.Model.Customer;
import com.example.demo.Model.Review;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ReviewRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BookingRepository bookingRepository;

	// Shows the admin login page
	@GetMapping("/login")
	public String showAdminLoginPage() {
		return "login/adminLogin"; // loads adminLogin.html
	}

	@PostMapping("/login")
	public String processAdminLogin(@RequestParam String username, @RequestParam String password, Model model) {

		// simplest hard-coded validation
		if ("admin".equals(username) && "admin123".equals(password))
			return "redirect:/admin/dashboard";

		// if login fails
		model.addAttribute("error", "Invalid Credentials");
		return "login/adminLogin";
	}

	@GetMapping("/dashboard")
	public String AdminDashboard() {
		return "dashboard/adminDashboard"; // loads adminDashboard.html
	}

	@GetMapping("/manageArtists")
	public String manageArtists(Model model) {
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		return "admin/manageArtists"; // loads manageArtists.html
	}

	@PostMapping("/updateArtistStatus")
	public String updateArtistStatus(@RequestParam Long artistId, @RequestParam String newStatus) {
		@SuppressWarnings("null")
		Artist artist = artistRepository.findById(artistId).orElse(null);
		if (artist != null) {
			artist.setStatus(newStatus);
			artistRepository.save(artist);
		}
		return "redirect:/admin/manageArtists";
	}

	@GetMapping("/artist/view/{id}")
	public String viewArtist(@PathVariable Long id, Model model) {
		@SuppressWarnings("null")
		Artist artist = artistRepository.findById(id).orElse(null);
		if (artist != null) {
			model.addAttribute("artist", artist);

			// fetch reviews for this artist
			List<Review> reviews = reviewRepository.findByArtistId(id);
			model.addAttribute("reviews", reviews);

			return "admin/viewArtist"; // loads viewArtist.html
		}
		return "redirect:/admin/manageArtists";
	}

	@SuppressWarnings("null")
	@GetMapping("/artist/delete/{id}")
	public String deleteArtist(@PathVariable Long id) {

		artistRepository.deleteById(id);

		return "redirect:/admin/manageArtists";
	}

	@GetMapping("/manageCustomers")
	public String manageCustomers(Model model) {
		List<Customer> customers = customerRepository.findAll();
		model.addAttribute("customers", customers);
		return "admin/manageCustomers"; // loads manageCustomers.html
	}

	@GetMapping("/customer/view/{id}")
	public String viewCustomer(@PathVariable Long id, Model model) {
		@SuppressWarnings("null")
		Customer customer = customerRepository.findById(id).orElse(null);
		if (customer != null) {
			model.addAttribute("customer", customer);

			// fetch reviews for this artist
			List<Review> reviews = reviewRepository.findByCustomerId(id);
			model.addAttribute("reviews", reviews);

			return "admin/viewCustomer"; // loads viewCustomer.html
		}
		return "redirect:/admin/manageCustomers";
	}

	@SuppressWarnings("null")
	@GetMapping("/customer/delete/{id}")
	public String deleteCustomer(@PathVariable Long id) {

		customerRepository.deleteById(id);

		return "redirect:/admin/manageCustomers";
	}

	@GetMapping("/viewBookings")
    public String viewBookings(Model model) {
		model.addAttribute("bookings", bookingRepository.findAll());
		return "admin/viewBookings";
    }
}
