package com.transaction_service.app.dto;

public record AccountResponse(
        Long accountId,
        String documentNumber
) {
}
