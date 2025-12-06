package com.example.demo.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Artist;
import com.example.demo.model.Booking;
import com.example.demo.model.Feedback;
import com.example.demo.model.PerformanceFile;
import com.example.demo.model.Review;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.PerformanceFileRepository;
import com.example.demo.repository.ReviewRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.logging.Logger;

@Controller
@RequestMapping("/artist")
public class ArtistController implements Serializable {
	private static final long serialVersionUID = 1905122041950251207L;

	transient Logger logger = Logger.getLogger(getClass().getName());

	// @Autowired
	private transient ArtistRepository artistRepo;
	// @Autowired
	private transient PerformanceFileRepository performanceFileRepo;
	// @Autowired
	private transient FeedbackRepository feedbackRepository;
	// @Autowired
	private transient ReviewRepository reviewRepository;
	// @Autowired
	private transient BookingRepository bookingRepository;

	private static final String ART = "artist";
	private static final String LOGIN = "redirect:/artist/login";
	private static final String LOGART = "loggedArtist";
	private static final String ART1 = "Artist";
	private static final String FB = "redirect:/artist/manageFeedbacks";
	private static final String BK = "redirect:/artist/manageBookings";

	public ArtistController(ArtistRepository artistRepo, PerformanceFileRepository performanceFileRepo, 
			FeedbackRepository feedbackRepository, ReviewRepository reviewRepository,
			BookingRepository bookingRepository) {
		this.artistRepo = artistRepo;
		this.performanceFileRepo = performanceFileRepo;
		this.feedbackRepository = feedbackRepository;
		this.reviewRepository = reviewRepository;
		this.bookingRepository = bookingRepository;
	}

	// Show Registration Page
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute(ART, new Artist());
		return "register/artistRegister";
	}

	// Handle Form Submission
	@PostMapping("/register")
	public String registerArtist(@ModelAttribute Artist artist) {
		// debug: log incoming artist fields
		logger.info("Registering Artist - name=" + artist.getName() + ", Email:" + artist.getEmail());
		artistRepo.save(artist); // Saves to DB
		return LOGIN; // Redirect after success
	}

	// Show login page
	@GetMapping("/login")
	public String showLoginPage(Model model) {
		model.addAttribute("artistLogin", new Artist());
		return "login/artistLogin";
	}

	// Handle login submission
	@PostMapping("/login")
	public String loginArtist(@RequestParam String email, @RequestParam String password, HttpSession session,
			Model model) {
		Artist loggedArtist = artistRepo.findByEmailAndPassword(email, password);

		if (loggedArtist != null) {
			session.setAttribute(LOGART, loggedArtist);
			return "redirect:/artist/dashboard"; // Replace with actual dashboard later
		}
		model.addAttribute("error", "Invalid email or password");
		return "login/artistLogin";
	}

	@GetMapping("/dashboard")
	public String artistDashboard(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in
		model.addAttribute(ART, artist);
		return "dashboard/artistDashboard"; // loads artistDashboard.html
	}

	@GetMapping("/manageProfile")
	public String manageProfile(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in
		model.addAttribute(ART, artist);
		return "artist/manageProfile"; // loads manageProfile.html
	}

	@PostMapping("/manageProfile")
	public String updateProfile(HttpSession session, @ModelAttribute Artist updatedArtist) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		// Update fields
		artist.setName(updatedArtist.getName());
		artist.setPhone(updatedArtist.getPhone());
		artist.setCategory(updatedArtist.getCategory());
		artist.setXp(updatedArtist.getXp());
		artist.setPrice(updatedArtist.getPrice());

		artistRepo.save(artist); // Save updated artist
		session.setAttribute(LOGART, artist); // Update session attribute
		return "redirect:/artist/manageProfile"; // Redirect back to profile management
	}

	@GetMapping("/manageFiles/index")
	public String manageFiles(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		List<PerformanceFile> files = performanceFileRepo.findAll(); // <-- load files
		model.addAttribute("files", files);
		return "artist/manageFiles/index"; // loads manageFiles/index.html
	}

	@PostMapping("/manageFiles/upload")
	public String uploadFile(HttpSession session, @RequestParam("file") MultipartFile file) throws IOException {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		if (!file.isEmpty()) {
			// Create PerformanceFile entity
			PerformanceFile performanceFile = new PerformanceFile();
			performanceFile.setFileName(file.getOriginalFilename());
			performanceFile.setFileType(file.getContentType());
			performanceFile.setData(file.getBytes());

			// Save to repository
			performanceFileRepo.save(performanceFile);
		}

		return "redirect:/artist/manageFiles/index"; // Redirect back to file management
	}

	@GetMapping("/files/{id}")
	public void showFile(HttpServletResponse response, @PathVariable Long id) throws IOException {
		PerformanceFile performanceFile = performanceFileRepo.findById(id).orElse(null);

		if (performanceFile != null) {
			response.setContentType(performanceFile.getFileType());
			response.setHeader("Content-Disposition", "inline; filename=\"" + performanceFile.getFileName() + "\"");
			response.getOutputStream().write(performanceFile.getData());
			response.getOutputStream().flush();
		}
	}

	@GetMapping("/deleteFile/{id}")
	public String deleteFile(HttpSession session, @PathVariable Long id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN;

		performanceFileRepo.deleteById(id);

		return "redirect:/artist/manageFiles/index";
	}

	// methods to perform CRUD operations on feedbacks given by artists to the admin
	// load feedback management page
	@GetMapping("/manageFeedbacks")
	public String manageFeedbacks(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		List<Feedback> feedbacks = feedbackRepository
				.findByUserTypeAndUserId(ART1, artist.getId());
		model.addAttribute("feedbacks", feedbacks);
		return "artist/manageFeedbacks"; // loads manageFeedbacks.html
	}

	// create new feedback
	@PostMapping("/createFeedback")
	public String createFeedback(HttpSession session, @RequestParam String message) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Feedback feedback = new Feedback();
		feedback.setUserType(ART1);
		feedback.setUserId(artist.getId());
		feedback.setUserName(artist.getName());
		feedback.setMessage(message);
		feedback.setDate(java.time.LocalDateTime.now());

		feedbackRepository.save(feedback);

		return FB; // Redirect back to feedback management
	}

	// edit existing feedback
	@PostMapping("/editFeedback")
	public String editFeedback(HttpSession session, @RequestParam Long id, @RequestParam String message) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Feedback feedback = feedbackRepository.findById(id).orElse(null);
		// If feedback not found or not owned by this artist, just redirect back
		if (feedback == null)
			return FB;

		// Ensure only the artist who created the feedback can edit it
		if (!ART1.equals(feedback.getUserType()) || feedback.getUserId() == null
				|| !feedback.getUserId().equals(artist.getId())) {
			return FB;
		}

		feedback.setMessage(message);
		feedback.setDate(java.time.LocalDateTime.now());
		feedbackRepository.save(feedback);

		return FB; // Redirect back to feedback management
	}

	// delete existing feedback
	@PostMapping("/deleteFeedback")
	public String deleteFeedback(HttpSession session, @RequestParam Long id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Feedback feedback = feedbackRepository.findById(id).orElse(null);
		// If feedback not found or not owned by this artist, just redirect back
		if (feedback == null)
			return FB;

		// Ensure only the artist who created the feedback can delete it
		if (!ART1.equals(feedback.getUserType()) || feedback.getUserId() == null
				|| !feedback.getUserId().equals(artist.getId())) {
			return FB;
		}

		feedbackRepository.deleteById(id);

		return FB; // Redirect back to feedback management
	}

	// method to view all reviews given by customers to the artist
	@GetMapping("/viewReviews")
	public String viewReviews(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		// Assuming there's a method in feedbackRepository to fetch reviews for the artist
		List<Review> reviews = reviewRepository.findByArtistId(artist.getId());
		model.addAttribute("reviews", reviews);
		return "artist/viewReviews"; // loads viewReviews.html
	}

	// method to view all bookings for the logged-in artist
	@GetMapping("/manageBookings")
	public String viewBookings(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		List<Booking> bookings = bookingRepository.findByArtistId(artist.getId());
		model.addAttribute("bookings", bookings);
		return "artist/manageBookings"; // loads manageBookings.html
	}

	// method to accept a booking request
	@PostMapping("/acceptBooking")
	public String acceptBooking(HttpSession session, @RequestParam int id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN;

		Booking booking = bookingRepository.findById(id).orElse(null);
		if (booking != null && booking.getArtistId() == artist.getId()) {
			booking.setStatus("Accepted");
			bookingRepository.save(booking);
		}
		return BK;
	}

	// method to deny a booking request
	@PostMapping("/denyBooking")
	public String denyBooking(HttpSession session, @RequestParam int id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN;

		Booking booking = bookingRepository.findById(id).orElse(null);
		if (booking != null && booking.getArtistId() == artist.getId()) {
			booking.setStatus("Denied");
			bookingRepository.save(booking);
		}
		return BK;
	}

	// method to edit bookings
	@PostMapping("/editBooking")
	public String editBooking(HttpSession session, @RequestParam int id, @RequestParam String newDate) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Booking booking = bookingRepository.findById(id).orElse(null);
		if (booking != null && booking.getArtistId() == artist.getId()) {
			booking.setDate(newDate);
			bookingRepository.save(booking);
		}
		return BK;
	}

	// method to cancel a booking
	@PostMapping("/cancelBooking")
	public String cancelBooking(HttpSession session, @RequestParam int id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Booking booking = bookingRepository.findById(id).orElse(null);
		if (booking != null && booking.getArtistId() == artist.getId()) {
			booking.setStatus("Cancelled");
			bookingRepository.save(booking);
		}
		return BK;
	}

	// method to complete a booking
	@PostMapping("/completeBooking")
	public String completeBooking(HttpSession session, @RequestParam int id) {
		Artist artist = (Artist) session.getAttribute(LOGART);
		if (artist == null)
			return LOGIN; // Redirect to login if not logged in

		Booking booking = bookingRepository.findById(id).orElse(null);
		if (booking != null && booking.getArtistId() == artist.getId()) {
			booking.setStatus("Completed");
			bookingRepository.save(booking);
		}
		return BK;
	}
}
