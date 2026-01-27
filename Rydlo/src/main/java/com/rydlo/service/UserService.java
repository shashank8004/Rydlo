package com.rydlo.service;

import com.rydlo.dto.UserRegDTO;
import com.rydlo.dto.UpdatePasswordDTO;

import jakarta.validation.Valid;

public interface UserService {

	 String addUser(@Valid UserRegDTO userDto);
	 
	 String updatePassword(com.rydlo.dto.UpdatePasswordDTO updatePasswordDTO);
}
