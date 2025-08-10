package com.transaction_service.app.service;

import com.transaction_service.app.dto.TransactionRequest;
import com.transaction_service.app.dto.TransactionResponse;
import org.springframework.stereotype.Component;


@Component
public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request);
}

