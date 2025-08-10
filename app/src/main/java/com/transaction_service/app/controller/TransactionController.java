package com.transaction_service.app.controller;

import com.transaction_service.app.dto.TransactionRequest;
import com.transaction_service.app.dto.TransactionResponse;
import com.transaction_service.app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
//        log.info("Received request to create transaction: {}", request.toString());
        return transactionService.createTransaction(request);
    }
}
