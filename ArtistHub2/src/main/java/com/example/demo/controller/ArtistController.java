package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
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
}
