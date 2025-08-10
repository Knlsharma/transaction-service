package com.transaction_service.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        Long accountId,
        int operationTypeId,
        BigDecimal amount,
        LocalDateTime eventDate
) {
}