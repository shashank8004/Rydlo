package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.ApiResponse;
import com.rydlo.dto.UpdatePasswordDTO;
import com.rydlo.dto.UserProfileDTO;
import com.rydlo.dto.UserRegDTO;
import com.rydlo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerUser(
			@RequestBody @Valid UserRegDTO userDto) {

		String message = userService.addUser(userDto);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse("SUCCESS", message));
	}

	@PutMapping("/update-password")
	public ResponseEntity<ApiResponse> updatePassword(
			@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
		String msg = userService.updatePassword(updatePasswordDTO);
		return ResponseEntity.ok(new ApiResponse("SUCCESS", msg));
	}

	@GetMapping("/profile")
	public ResponseEntity<UserProfileDTO> getUserProfile() {
		return ResponseEntity.ok(userService.getProfile());
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiResponse> updateUserProfile(@RequestBody @Valid UserProfileDTO profileDTO) {
		String msg = userService.updateProfile(profileDTO);
		return ResponseEntity.ok(new ApiResponse("SUCCESS", msg));
	}
}