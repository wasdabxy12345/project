package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Model.Artist;
import com.example.demo.repository.ArtistRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/artist")
public class ArtistController {

	private final ArtistRepository artistRepo;

	public ArtistController(ArtistRepository artistRepo) {
		this.artistRepo = artistRepo;
	}

	// Show Registration Page
	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute("artist", new Artist());
		return "register/artistRegister";
	}

	// Handle Form Submission
	@SuppressWarnings("null")
	@PostMapping("/register")
	public String registerArtist(@ModelAttribute Artist artist) {
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

}
