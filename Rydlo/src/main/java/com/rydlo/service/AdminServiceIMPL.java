package com.rydlo.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.Repository.UserRepository;
import com.rydlo.entities.User;

import lombok.AllArgsConstructor;


@Service
@Transactional
@AllArgsConstructor

public class AdminServiceIMPL implements AdminService {

	@Autowired
	private final ModelMapper modelMapper;
	private final UserRepository userRepository;
	
	@Override
	public List<User> getAllUsers() {
		
		List<User> userList = userRepository.findAll();
		return userList;
	}

}
