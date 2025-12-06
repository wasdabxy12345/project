package com.example.demo.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Artist;
import com.example.demo.model.Customer;
import com.example.demo.model.Feedback;
import com.example.demo.model.Review;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.ReviewRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final ArtistRepository artistRepository;
	private final CustomerRepository customerRepository;
	private final ReviewRepository reviewRepository;
	private final BookingRepository bookingRepository;
	private final FeedbackRepository feedbackRepository;

	private static final String MANAGEARTISTS = "redirect:/admin/manageArtists";
	private static final String REV = "reviews";

	public AdminController(ArtistRepository artistRepository, CustomerRepository customerRepository,
			ReviewRepository reviewRepository, BookingRepository bookingRepository,
			FeedbackRepository feedbackRepository) {
		this.artistRepository = artistRepository;
		this.customerRepository = customerRepository;
		this.reviewRepository = reviewRepository;
		this.bookingRepository = bookingRepository;
		this.feedbackRepository = feedbackRepository;
	}

	// Shows the admin login page
	@GetMapping("/login")
	public String showAdminLoginPage() {
		return "login/adminLogin"; // loads adminLogin.html
	}

	@PostMapping("/login")
	public String processAdminLogin(@RequestParam String username, @RequestParam String password, Model model) {

		// hardcoded validation
		if ("admin".equals(username) && "admin123".equals(password))
			return "redirect:/admin/dashboard";

		// if login fails
		model.addAttribute("error", "Invalid Credentials");
		return "login/adminLogin";
	}

	@GetMapping("/dashboard")
	public String adminDashboard() {
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
		Artist artist = artistRepository.findById(artistId).orElse(null);
		if (artist != null) {
			artist.setStatus(newStatus);
			artistRepository.save(artist);
		}
		return MANAGEARTISTS;
	}

	@GetMapping("/artist/view/{id}")
	public String viewArtist(@PathVariable Long id, Model model) {
		Artist artist = artistRepository.findById(id).orElse(null);
		if (artist != null) {
			model.addAttribute("artist", artist);

			// fetch reviews for this artist
			List<Review> reviews = reviewRepository.findByArtistId(id);
			model.addAttribute(REV, reviews);

			return "admin/viewArtist"; // loads viewArtist.html
		}
		return MANAGEARTISTS;
	}

	@GetMapping("/artist/delete/{id}")
	public String deleteArtist(@PathVariable Long id) {

		artistRepository.deleteById(id);

		return MANAGEARTISTS;
	}

	@GetMapping("/manageCustomers")
	public String manageCustomers(Model model) {
		List<Customer> customers = customerRepository.findAll();
		model.addAttribute("customers", customers);
		return "admin/manageCustomers"; // loads manageCustomers.html
	}

	@GetMapping("/customer/view/{id}")
	public String viewCustomer(@PathVariable Long id, Model model) {
		Customer customer = customerRepository.findById(id).orElse(null);
		if (customer != null) {
			model.addAttribute("customer", customer);

			// fetch reviews for this artist
			List<Review> reviews = reviewRepository.findByCustomerId(id);
			model.addAttribute(REV, reviews);

			return "admin/viewCustomer"; // loads viewCustomer.html
		}
		return "redirect:/admin/manageCustomers";
	}

	@GetMapping("/customer/delete/{id}")
	public String deleteCustomer(@PathVariable Long id) {

		customerRepository.deleteById(id);

		return "redirect:/admin/manageCustomers";
	}

	@GetMapping("/viewBookings")
    public String viewBookings(Model model) {
		model.addAttribute("viewBookings", bookingRepository.findAll());
		return "admin/viewBookings";
    }

	@GetMapping("/viewReviews")
	public String viewReviews(Model model) {
		List<Review> reviews = reviewRepository.findAll();
		model.addAttribute(REV, reviews);

		return "admin/viewReviews";
	}

	@GetMapping("/viewFeedbacks")
	public String viewFeedbacks(Model model) {
		List<Feedback> feedbacks = feedbackRepository.findAll();
		model.addAttribute("feedbacks", feedbacks);

		return "admin/viewFeedbacks";
	}
}
