package com.transaction_service.app.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "Account ID is required")
        Long accountId,

        @Min(value = 1, message = "Operation type must be between 1 and 4")
        @Max(value = 4, message = "Operation type must be between 1 and 4")
        int operationTypeId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Amount must have up to 2 decimal places")
        BigDecimal amount
) {
}