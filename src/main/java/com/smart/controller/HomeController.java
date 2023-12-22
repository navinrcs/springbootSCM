package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/home")
	public String home(Model m) {
		m.addAttribute("title", "Home-smart contact manager");

		return "home";

	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About-smart contact manager");

		return "about";

	}

	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Signup-smart contact manager");
		m.addAttribute("user", new User());

		return "signup";

	}

	@PostMapping("/doregister")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean Agreement, Model m,
			HttpSession session) {

		if (result1.hasErrors()) {
			System.out.println(result1);

			return "signup";

		}

		try {
			if (!Agreement) {

				System.out.println(Agreement);
				throw new Exception("you have not agreed terms and condition");

			}
			System.out.println(Agreement);
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("default.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = userRepository.save(user);
			m.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully registered!!", "alert-success"));
			return "signup";
		} catch (Exception e) {

			e.printStackTrace();
			m.addAttribute("user", user);

			session.setAttribute("message", new Message("something went wrong!!" + e.getMessage(), "alert-danger"));
			return "signup";

		}

	}

	@GetMapping("/signin")
	public String customlogin(Model model) {

		
		model.addAttribute("title", "signin");
		
		System.out.println("github");
		
		return "login";
		
	}
}
