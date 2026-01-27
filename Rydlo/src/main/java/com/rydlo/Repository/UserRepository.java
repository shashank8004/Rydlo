package com.rydlo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	

}
