package com.rydlo.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.DuplicateResourceException;
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

	@Autowired
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
    	            passwordEncoder.encode(user.getPassword())
    	    );

    	    User persistentUser = userRepository.save(user);

    	    return "User added with user id = " + persistentUser.getId();
    	}

	@Override
	public String updatePassword(com.rydlo.dto.UpdatePasswordDTO updatePasswordDTO) {
		org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		// In a real app we would cast to UserPrincipal, let's assume standard security context
		String email = authentication.getName(); // Usually email or username

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new com.rydlo.custom_exception.ResourceNotFoundException("User not found"));

		// 1. Check old password
		if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
			throw new com.rydlo.custom_exception.ApiException("Invalid old password"); // ApiException needs to exist or use IllegalArgument
		}

		// 2. Update new password
		user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
		userRepository.save(user);

		return "Password updated successfully";
	}
}