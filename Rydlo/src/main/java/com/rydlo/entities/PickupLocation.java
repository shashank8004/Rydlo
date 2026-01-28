package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



import jakarta.validation.constraints.Pattern;

@Table(name="pickup_location")
@AttributeOverride(name = "id",column = @Column(name="pickup_location_id"))
@ToString(callSuper = true,exclude = {"bikeDetails"})
@Getter
@Setter
@Entity
public class PickupLocation extends BaseEntity {

	
	

	    private String  street;
	    private String  locality;
	    private String city;
	    private String state;
	    
	    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be exactly 6 digits")
	    private String pincode;
	    
	   
	    
	

}
