package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.Contact;

public interface ContactRepository  extends JpaRepository<Contact, Integer> {
	
	
//	
//	@Query("from Contact as c where c.user.id = :userId")
//	public List<Contact> getContactsByUser(@RequestParam("userId") int userId)

	/* PAGINATION IMPLEMENTTION */
	
	@Query("from Contact as c where c.user.id = :userId")
	public Page<Contact>  findContactByUser(@RequestParam("userId") int userId, Pageable page );
	
	

}