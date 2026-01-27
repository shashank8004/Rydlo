package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@ToString(callSuper = true,exclude = "password")
@Table(name = "users")
public class User extends BaseEntity {

	@Column(name="first_name", nullable = false, length = 50)
	private String firstName;
	
	@Column(name="last_name", nullable = false, length = 50)
	private String lastName;
	
	@Column(nullable = false,length = 30,unique = true)
	private String email;
	
	@Column(nullable = false,length = 13,unique = true)
	private String phone;
	
 
	
	@Column(nullable = false)
	private String password;
	
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Enumerated(EnumType.STRING)
	private Status status=Status.ACTIVE;
	
}
