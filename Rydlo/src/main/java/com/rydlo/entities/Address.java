package com.rydlo.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Address {

    private String  houseNo;
    private String  locality;
    private String city;
    private String state;
    private String pincode;
}
