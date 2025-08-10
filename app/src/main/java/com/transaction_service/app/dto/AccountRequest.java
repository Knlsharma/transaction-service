package com.transaction_service.app.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountRequest(
        @NotBlank(message = "Enter valid document number.")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Document number must be of 11 digits"
        )
        String documentNumber
) {
}
