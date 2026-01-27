package com.rydlo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {

	boolean existsByEmailAndPhone(String email, String phone);

	Optional<User> findByEmail (String email);

	boolean existsByPhone(String phone);

	boolean existsByEmail(String email);
	
	

}
