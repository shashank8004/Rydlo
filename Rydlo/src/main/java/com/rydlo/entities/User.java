package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@AttributeOverrides({
    @AttributeOverride(name = "address.houseNo", column = @Column(name = "house_no")),
    @AttributeOverride(name = "address.locality", column = @Column(name = "locality")),
    @AttributeOverride(name = "address.city", column = @Column(name = "city")),
    @AttributeOverride(name = "address.state", column = @Column(name = "state")),
    @AttributeOverride(name = "address.pincode", column = @Column(name = "pincode"))
})
@ToString(callSuper = true,exclude = "password")
@Table(name = "users")
public class User extends BaseEntity {

	@Column(length = 50)
	private String name;
	
	@Column(length = 30)
	private String email;
	
	@Column(length = 13)
	private String phone;
	

	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Embedded
	private Address address;
	
}
