package com.rydlo.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ApiException;
import com.rydlo.custom_exception.DuplicateResourceException;
import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.UpdatePasswordDTO;
import com.rydlo.dto.UserProfileDTO;
import com.rydlo.dto.UserRegDTO;
import com.rydlo.entities.Role;
import com.rydlo.entities.User;
import com.rydlo.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceIMPL implements UserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public String addUser(@Valid UserRegDTO userDto) {

		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new DuplicateResourceException("Email already exists");
		}

		if (userRepository.existsByPhone(userDto.getPhone())) {
			throw new DuplicateResourceException("Phone already exists");
		}

		if (userDto.getRole() == Role.ROLE_ADMIN) {
			throw new IllegalArgumentException("Admin registration not allowed");
		}

		User user = modelMapper.map(userDto, User.class);

		user.setPassword(
				passwordEncoder.encode(user.getPassword()));

		User persistentUser = userRepository.save(user);

		return "User added with user id = " + persistentUser.getId();
	}

	@Override
	public String updatePassword(UpdatePasswordDTO updatePasswordDTO) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// 1. Check old password
		if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
			throw new ApiException("Invalid old password");
		}

		user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
		userRepository.save(user);

		return "Password updated successfully";
	}

	@Override
	public UserProfileDTO getProfile() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return modelMapper.map(user, UserProfileDTO.class);
	}

	@Override
	public String updateProfile(UserProfileDTO profileDTO) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Check if phone is being updated and if it already exists for another user
		if (!user.getPhone().equals(profileDTO.getPhone())) {
			if (userRepository.existsByPhone(profileDTO.getPhone())) {
				throw new DuplicateResourceException(
						"Phone number already in use by another account");
			}
		}

		user.setFirstName(profileDTO.getFirstName());
		user.setLastName(profileDTO.getLastName());
		user.setPhone(profileDTO.getPhone());

		userRepository.save(user);

		return "Profile updated successfully";
	}
}