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

@Entity
@Table(name="owners")
@Getter
@Setter
@AttributeOverride(name = "id",column = @Column(name="owner_id"))

@ToString(callSuper = true,exclude = "user")

public class Owner extends BaseEntity {
    
	@Column(name ="company_name")
	private String companyName;

    @Column(name="gst_number",unique = true)
    private String gstNumber;

    @Embedded
    private Address address;
	
    
	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
}
