package com.transaction_service.app.utils;

import com.transaction_service.app.enums.OperationType;
import com.transaction_service.app.exception.InvalidTransactionException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class AppUtil {

    public static OperationType parseOperationType(Integer operationTypeId) {
        try {
            return OperationType.fromId(operationTypeId);
        } catch (IllegalArgumentException e) {
//            log.error("Invalid operation type: {}", operationTypeId);
            throw new InvalidTransactionException("Invalid operation type: " + operationTypeId);
        }
    }

    public static BigDecimal validateAndConvertAmount(BigDecimal amount, OperationType type) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            log.error("Invalid amount: {}", amount);
            throw new InvalidTransactionException("Entered amount should be always positive");
        }

        return amount.multiply(BigDecimal.valueOf(type.getSign()));
    }

}
