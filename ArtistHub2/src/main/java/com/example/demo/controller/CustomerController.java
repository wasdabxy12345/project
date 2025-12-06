package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Artist;
import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.model.Feedback;
import com.example.demo.model.PerformanceFile;
import com.example.demo.model.Review;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.PerformanceFileRepository;
import com.example.demo.repository.ReviewRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerRepository customerRepo;
    private final ArtistRepository artistRepo;
	private final PerformanceFileRepository performanceFileRepo;
	private final BookingRepository bookingRepo;
	private final FeedbackRepository feedbackRepo;
	private final ReviewRepository reviewRepo;

	private static final String CUS = "customer";
	private static final String LOGIN = "redirect:/customer/login";
	private static final String LC = "loggedCustomer";
	private static final String CUS1 = "Customer";
	private static final String VF = "redirect:/customer/viewFeedbacks";
	private static final String VR = "redirect:/customer/viewReviews";

	public CustomerController(CustomerRepository customerRepo, ArtistRepository artistRepo, PerformanceFileRepository performanceFileRepo, BookingRepository bookingRepo, FeedbackRepository feedbackRepo, ReviewRepository reviewRepo) {
		this.customerRepo = customerRepo;
		this.artistRepo = artistRepo;
		this.performanceFileRepo = performanceFileRepo;
		this.bookingRepo = bookingRepo;
		this.feedbackRepo = feedbackRepo;
		this.reviewRepo = reviewRepo;
	}

	// Show Registration Page
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute(CUS, new Customer());
		return "register/customerRegister";
	}

	// Handle Form Submission
	@PostMapping("/register")
	public String registerCustomer(@ModelAttribute Customer customer) {
		customerRepo.save(customer); // Saves to DB
		return LOGIN; // Redirect after success
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
			session.setAttribute(LC, loggedCustomer);
			return "redirect:/customer/dashboard";
		}
		model.addAttribute("error", "Invalid email or password");
		return "login/customerLogin";
	}

	@GetMapping("/dashboard")
	public String customerDashboard(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;
		model.addAttribute(CUS, customer);
		return "dashboard/customerDashboard";
	}

	// method to manage profile
	@GetMapping("/manageProfile")
	public String manageProfile(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;
		model.addAttribute(CUS, customer);
		return "customer/manageProfile";
	}

	@PostMapping("/manageProfile")
	public String updateProfile(HttpSession session, @ModelAttribute Customer updatedCustomer) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		customer.setName(updatedCustomer.getName());
		customer.setEmail(updatedCustomer.getEmail());
		customer.setPassword(updatedCustomer.getPassword());
		customerRepo.save(customer);
		return "redirect:/customer/manageProfile";
	}

	// methtod to view artists details and search by category
	@GetMapping("/searchArtist")
	public String searchArtist(HttpSession session, Model model, String category) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;
		List<String> allCategories = artistRepo.findDistinctCategories();
		model.addAttribute("allCategories", allCategories);
		model.addAttribute("selectedCategory", category);
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
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;
		
		Artist artist = artistRepo.findById(id).orElse(null);
		if (artist == null)
			return "redirect:/customer/searchArtist";
		
		List<PerformanceFile> portfolio = performanceFileRepo.findByArtistId(id);
		
		model.addAttribute("artist", artist);
		model.addAttribute("portfolio", portfolio);
		return "customer/viewArtist";
	}

	// method to book an artist
	@PostMapping("/bookArtist")
	public String bookArtist(HttpSession session, @RequestParam Long artistId, @RequestParam String date, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;
		
		Artist artist = artistRepo.findById(artistId).orElse(null);
		if (artist == null)
			return "redirect:/customer/searchArtist";
		
		Booking booking = new Booking();
		booking.setArtistId((int) (long) artistId);
		booking.setCustomerId((int) (long) customer.getId());
		booking.setArtistName(artist.getName());
		booking.setCustomerName(customer.getName());
		booking.setDate(date);
		booking.setStatus("PENDING");
		
		bookingRepo.save(booking);
		
		return "redirect:/customer/viewArtist/" + artistId;
	}

	// method to view all bookings for the logged-in customer
	@GetMapping("/viewBookings")
	public String viewBookings(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		List<Booking> bookings = bookingRepo.findByCustomerId((long) customer.getId());
		model.addAttribute("bookings", bookings);
		return "customer/viewBookings";
	}

	// method to cancel a booking by customer
	@PostMapping("/cancelBooking")
	public String cancelBooking(HttpSession session, @RequestParam int id) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		Booking booking = bookingRepo.findById(id).orElse(null);
		if (booking != null && booking.getCustomerId() == customer.getId()) {
			booking.setStatus("Cancelled");
			bookingRepo.save(booking);
		}
		return "redirect:/customer/viewBookings";
	}

	// method to view feedbacks for the logged-in customer
	@GetMapping("/viewFeedbacks")
	public String viewFeedbacks(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		List<Feedback> feedbacks = feedbackRepo.findByUserTypeAndUserId(CUS1, customer.getId());
		model.addAttribute("feedbacks", feedbacks);
		model.addAttribute("feedback", new Feedback());
		return "customer/viewFeedbacks";
	}

	// method to add new feedback
	@PostMapping("/addFeedback")
	public String addFeedback(HttpSession session, @RequestParam String message) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		Feedback feedback = new Feedback();
		feedback.setUserType(CUS1);
		feedback.setUserId(customer.getId());
		feedback.setUserName(customer.getName());
		feedback.setMessage(message);
		feedback.setDate(LocalDateTime.now());

		feedbackRepo.save(feedback);

		return VF;
	}

	// method to edit existing feedback
	@PostMapping("/editFeedback")
	public String editFeedback(HttpSession session, @RequestParam Long id, @RequestParam String message) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		Feedback feedback = feedbackRepo.findById(id).orElse(null);
		if (feedback == null)
			return VF;

		if (!CUS1.equals(feedback.getUserType()) || !feedback.getUserId().equals(customer.getId())) {
			return VF;
		}

		feedback.setMessage(message);
		feedback.setDate(LocalDateTime.now());
		feedbackRepo.save(feedback);

		return VF;
	}

	// method to delete feedback
	@PostMapping("/deleteFeedback")
	public String deleteFeedback(HttpSession session, @RequestParam Long id) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		Feedback feedback = feedbackRepo.findById(id).orElse(null);
		if (feedback == null)
			return VF;

		if (!CUS1.equals(feedback.getUserType()) || !feedback.getUserId().equals(customer.getId())) {
			return VF;
		}

		feedbackRepo.deleteById(id);

		return VF;
	}

	// method to view reviews for the logged-in customer
	@GetMapping("/viewReviews")
	public String viewReviews(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		List<Review> reviews = reviewRepo.findByCustomerId(customer.getId());
		List<Booking> completedBookings = bookingRepo.findByCustomerId((long) customer.getId());
		completedBookings = completedBookings.stream()
			.filter(b -> "Completed".equals(b.getStatus()))
			.toList();

		model.addAttribute("reviews", reviews);
		model.addAttribute("completedBookings", completedBookings);
		return "customer/viewReviews";
	}

	// method to add new review
	@PostMapping("/addReview")
	public String addReview(HttpSession session, @RequestParam Long artistId, @RequestParam String comment, @RequestParam int rating) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		if (rating < 1 || rating > 5)
			return VR;

		Artist artist = artistRepo.findById(artistId).orElse(null);
		if (artist == null)
			return VR;

		Review review = new Review();
		review.setArtistId(artistId);
		review.setCustomerId(customer.getId());
		review.setArtistName(artist.getName());
		review.setCustomerName(customer.getName());
		review.setComment(comment);
		review.setRating(rating);
		review.setDate(LocalDateTime.now());

		reviewRepo.save(review);

		return VR;
	}

	// method to edit existing review
	@PostMapping("/editReview")
	public String editReview(HttpSession session, @RequestParam Long id, @RequestParam String comment, @RequestParam int rating) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		if (rating < 1 || rating > 5)
			return VR;

		Review review = reviewRepo.findById(id).orElse(null);
		if (review == null)
			return VR;

		if (!review.getCustomerId().equals(customer.getId())) {
			return VR;
		}

		review.setComment(comment);
		review.setRating(rating);
		review.setDate(LocalDateTime.now());
		reviewRepo.save(review);

		return VR;
	}

	// method to delete review
	@PostMapping("/deleteReview")
	public String deleteReview(HttpSession session, @RequestParam Long id) {
		Customer customer = (Customer) session.getAttribute(LC);
		if (customer == null)
			return LOGIN;

		Review review = reviewRepo.findById(id).orElse(null);
		if (review == null)
			return VR;

		if (!review.getCustomerId().equals(customer.getId())) {
			return VR;
		}

		reviewRepo.deleteById(id);

		return VR;
	}
}
