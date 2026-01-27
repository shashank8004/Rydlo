package com.rydlo.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@ToString(exclude = {"creationTime","updationTime"})
@Getter
@Setter
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "creation_time")
	@CreationTimestamp
	private LocalDateTime creationTime;
	
	@UpdateTimestamp
	@Column(name = "updation_time")
	private LocalDateTime updationTime;
}
