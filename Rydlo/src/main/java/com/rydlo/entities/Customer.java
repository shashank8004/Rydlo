package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "customers")
@Entity
@Getter
@Setter
@AttributeOverride(name = "id",column = @Column(name="customer_id"))
@ToString(callSuper = true,exclude = "user")
public class Customer extends BaseEntity {

	@Column(nullable = false,length = 30,name ="driving_licence")
	private String drivingLicence;
	
	
	@Column(name ="is_verified")
	private boolean isVerified;
	
	@Embedded
	private Address address;
	
	
	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
}
