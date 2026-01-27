package com.rydlo.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class PickupLocation {

	
	

	    private String  street;
	    private String  locality;
	    private String city;
	    private String state;
	    private String pincode;
	

}
