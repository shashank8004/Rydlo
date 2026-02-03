package com.rydlo.service;

import com.rydlo.dto.UpdatePasswordDTO;
import com.rydlo.dto.UserProfileDTO;
import com.rydlo.dto.UserRegDTO;

import jakarta.validation.Valid;

public interface UserService {

	String addUser(@Valid UserRegDTO userDto);

	String updatePassword(UpdatePasswordDTO updatePasswordDTO);

	UserProfileDTO getProfile();

	String updateProfile(UserProfileDTO profileDTO);
}
