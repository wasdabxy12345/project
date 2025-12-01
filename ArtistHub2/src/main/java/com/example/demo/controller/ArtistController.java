package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Artist;
import com.example.demo.Model.Feedback;
import com.example.demo.Model.PerformanceFile;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.PerformanceFileRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/artist")
public class ArtistController {

	@Autowired
	private final ArtistRepository artistRepo;
	@Autowired
	private final PerformanceFileRepository performanceFileRepo;
	@Autowired
	private FeedbackRepository feedbackRepository;

	public ArtistController(ArtistRepository artistRepo, PerformanceFileRepository performanceFileRepo) {
		this.artistRepo = artistRepo;
		this.performanceFileRepo = performanceFileRepo;
	}

	// Show Registration Page
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute("artist", new Artist());
		return "register/artistRegister";
	}

	// Handle Form Submission
	@PostMapping("/register")
	public String registerArtist(@ModelAttribute Artist artist) {
		// debug: log incoming artist fields
		System.out.println("Registering Artist - name=" + artist.getName() + ", Email:" + artist.getEmail());
		artistRepo.save(artist); // Saves to DB
		return "redirect:/artist/login"; // Redirect after success
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
			session.setAttribute("loggedArtist", loggedArtist);
			return "redirect:/artist/dashboard"; // Replace with actual dashboard later
		}
		model.addAttribute("error", "Invalid email or password");
		return "login/artistLogin";
	}

	@GetMapping("/dashboard")
	public String artistDashboard(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in
		model.addAttribute("artist", artist);
		return "dashboard/artistDashboard"; // loads artistDashboard.html
	}

	@GetMapping("/manageProfile")
	public String manageProfile(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in
		model.addAttribute("artist", artist);
		return "artist/manageProfile"; // loads manageProfile.html
	}

	@PostMapping("/manageProfile")
	public String updateProfile(HttpSession session, @ModelAttribute Artist updatedArtist) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in

		// Update fields
		artist.setName(updatedArtist.getName());
		artist.setPhone(updatedArtist.getPhone());
		artist.setCategory(updatedArtist.getCategory());
		artist.setXp(updatedArtist.getXp());
		artist.setPrice(updatedArtist.getPrice());

		artistRepo.save(artist); // Save updated artist
		session.setAttribute("loggedArtist", artist); // Update session attribute
		return "redirect:/artist/manageProfile"; // Redirect back to profile management
	}

	@GetMapping("/manageFiles/index")
	public String manageFiles(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in

		List<PerformanceFile> files = performanceFileRepo.findAll(); // <-- load files
		model.addAttribute("files", files);
		return "artist/manageFiles/index"; // loads manageFiles/index.html
	}

	@PostMapping("/manageFiles/upload")
	public String uploadFile(HttpSession session, @RequestParam("file") MultipartFile file) throws IOException {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in

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
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login";

		performanceFileRepo.deleteById(id);

		return "redirect:/artist/manageFiles/index";
	}

	// methods to perform CRUD operations on feedbacks given by artists to the admin
	// load feedback management page
	@GetMapping("/manageFeedbacks")
	public String manageFeedbacks(HttpSession session, Model model) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in

		List<Feedback> feedbacks = feedbackRepository
				.findByUserTypeAndUserId("Artist", artist.getId());
		model.addAttribute("feedbacks", feedbacks);
		return "artist/manageFeedbacks"; // loads manageFeedbacks.html
	}

	// create new feedback
	@PostMapping("/createFeedback")
	public String createFeedback(HttpSession session, @RequestParam String message) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login"; // Redirect to login if not logged in

		Feedback feedback = new Feedback();
		feedback.setUserType("Artist");
		feedback.setUserId(artist.getId());
		feedback.setUserName(artist.getName());
		feedback.setMessage(message);
		feedback.setDate(java.time.LocalDateTime.now());

		feedbackRepository.save(feedback);

		return "redirect:/artist/manageFeedbacks"; // Redirect back to feedback management
	}

	// edit existing feedback
	@PostMapping("/editFeedback")
	public String editFeedback(HttpSession session, @RequestParam Long id, @RequestParam String message) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login";

		Feedback feedback = feedbackRepository.findById(id).orElse(null);
		// If feedback not found or not owned by this artist, just redirect back
		if (feedback == null)
			return "redirect:/artist/manageFeedbacks";

		// Ensure only the artist who created the feedback can edit it
		if (!"Artist".equals(feedback.getUserType()) || feedback.getUserId() == null
				|| !feedback.getUserId().equals(artist.getId())) {
			return "redirect:/artist/manageFeedbacks";
		}

		feedback.setMessage(message);
		feedback.setDate(java.time.LocalDateTime.now());
		feedbackRepository.save(feedback);

		return "redirect:/artist/manageFeedbacks";
	}

	// delete existing feedback
	@PostMapping("/deleteFeedback")
	public String deleteFeedback(HttpSession session, @RequestParam Long id) {
		Artist artist = (Artist) session.getAttribute("loggedArtist");
		if (artist == null)
			return "redirect:/artist/login";

		Feedback feedback = feedbackRepository.findById(id).orElse(null);
		// If feedback not found or not owned by this artist, just redirect back
		if (feedback == null)
			return "redirect:/artist/manageFeedbacks";

		// Ensure only the artist who created the feedback can delete it
		if (!"Artist".equals(feedback.getUserType()) || feedback.getUserId() == null
				|| !feedback.getUserId().equals(artist.getId())) {
			return "redirect:/artist/manageFeedbacks";
		}

		feedbackRepository.deleteById(id);

		return "redirect:/artist/manageFeedbacks";
	}
}
