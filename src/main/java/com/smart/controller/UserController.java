package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repository;

	@Autowired
	private ContactRepository contactRepository;

	// Method for adding common data to response

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String name = principal.getName();

		User user = repository.getUserByUserName(name);

		// System.out.println(uName);

		model.addAttribute("user", user);

	}

	// Home

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");

		return "/normal/userdashboard";

	}

	// Add contact form handler

	@GetMapping("/addcontact")
	public String addContactForm(Model model) {

		model.addAttribute("title", "add contact");
		model.addAttribute("contact", new Contact());

		return "/normal/addcontactform";

	}

	// Processing add contact form

	@PostMapping("/processcontact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal) {

		try {
			String name = principal.getName();

			User user = repository.getUserByUserName(name);

			if (file.isEmpty()) {

				// if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.png");

			} else {

				// upload the file to folder and update image in the contact

				contact.setImage(file.getOriginalFilename());

				File file2 = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("File is uploaded");

			}
			contact.setUser(user);

			user.getContacts().add(contact);

			this.repository.save(user);

			System.out.println("Contact Details:" + contact);

		} catch (Exception e) {
			System.out.println("Message:" + e.getMessage());
			e.printStackTrace();
		}

		return "/normal/addcontactform";
	}

	// @GetMapping("/showcontact")
	@GetMapping("/showcontact/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		m.addAttribute("title", "Show contacts");

		String name = principal.getName();

		User user = this.repository.getUserByUserName(name);

		Pageable pageable = PageRequest.of(page, 3);

		// List<Contact> contacts =
		// this.contactRepository.getContactsByUser(user.getId());

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentpage", page);
		m.addAttribute("totalpages", contacts.getTotalPages());

		return "/normal/showcontacts";

	}

	@GetMapping("/contact/{cid}")
	public String showSpecificContact(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		Optional<Contact> oc = this.contactRepository.findById(cid);

		Contact contact = oc.get();

		String name = principal.getName();
		User user = this.repository.getUserByUserName(name);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);

		}
		return "/normal/contactdetail";

	}

	@GetMapping("/deletecontact/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, HttpSession session
			) {

		Contact contact = this.contactRepository.findById(cid).get();

		contact.setUser(null);
		this.contactRepository.delete(contact);

		// Remove image from contact
		

			session.setAttribute("message", new Message("contact deleted successfully..", "alert-success"));

		

		// contact.getImage();

		return "redirect:/user/showcontact/0";

	}

	@PostMapping("/updatecontact/{cid}")

	public String updateContact(@PathVariable("cid") Integer cid, Model model) {

		Contact contact = this.contactRepository.findById(cid).get();
		System.out.println(contact);

		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact");

		return "/normal/updatecontact";
	}

	@PostMapping("/processupdate")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			Contact oldcontact = this.contactRepository.findById(contact.getCid()).get();
			if (!file.isEmpty()) {

				// DELETE OLD IMAGE
				File deletefile = new ClassPathResource("static/images").getFile();
				File file3 = new File(deletefile, oldcontact.getImage());

				file3.delete();

				// UPLOAD NEW IMAGE
				File file2 = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {

				contact.setImage(oldcontact.getImage());

			}

			String name = principal.getName();

			User user = this.repository.getUserByUserName(name);

			contact.setUser(user);

			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact Updated Successfully..", "alert-success"));

		} catch (Exception e) {

			e.printStackTrace();
			session.setAttribute("message", new Message("something went wrong!!", "alert-danger"));

		}
		System.out.println("Contact id:" + contact.getCid());

		return "redirect:/user/contact/" + contact.getCid();

	}
	
	//Profile handler
	
	
	@GetMapping("/profile")
	public String profilehandle(Model model) {
		
		model.addAttribute("title", "Your profile");
		
		
		
	return "/normal/profile";
	
	}
	
	
	

}
