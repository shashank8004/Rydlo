package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.ApiResponse;
import com.rydlo.dto.UserRegDTO;
import com.rydlo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
	
	@Autowired
	private final  UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerUser(
	        @RequestBody @Valid UserRegDTO userDto) {

	    String message = userService.addUser(userDto);

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(new ApiResponse("SUCCESS", message));
	}
	
	@org.springframework.web.bind.annotation.PutMapping("/update-password")
	public ResponseEntity<ApiResponse> updatePassword(@RequestBody @Valid com.rydlo.dto.UpdatePasswordDTO updatePasswordDTO) {
		String msg = userService.updatePassword(updatePasswordDTO);
		return ResponseEntity.ok(new ApiResponse("SUCCESS", msg));
	}
	

	
}