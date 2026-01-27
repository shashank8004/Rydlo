package com.rydlo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DropOffRequestDTO {

    @NotNull
    private Long bookingId;

    @Min(value = 0, message = "Final km must be >= 0")
    private int finalKm;

 
}
