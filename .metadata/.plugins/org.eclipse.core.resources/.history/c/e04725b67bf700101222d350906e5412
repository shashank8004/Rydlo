package com.rydlo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.entities.User;
import com.rydlo.service.AdminService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequestMapping("/admin")
@RestController

@AllArgsConstructor
public class AdminController {
	
	
	@Autowired
	private final AdminService adminService;
	
	@GetMapping("/all")
	public List<User> getAllUsers()
	{
		List <User> userList= adminService.getAllUsers();
		
		return userList;
	}
	
	

}
