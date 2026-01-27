package com.rydlo.service;

import com.rydlo.dto.UserRegDTO;

import jakarta.validation.Valid;

public interface UserService {

	 String addUser(@Valid UserRegDTO userDto);

}
