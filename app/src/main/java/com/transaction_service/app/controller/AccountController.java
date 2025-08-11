package com.transaction_service.app.controller;

import com.transaction_service.app.dto.AccountRequest;
import com.transaction_service.app.dto.AccountResponse;
import com.transaction_service.app.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request
    ) {

        AccountResponse response = accountService.processAccountRequest(request);
        log.info("Able to create account for request  : {} and response : {}", request.toString(), response.toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            value = "/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AccountResponse getAccount(
            @PathVariable(required = true)
            @Positive(message = "Account ID must be greater than 0")
            Long accountId
    ) {
        return accountService.fetchAccountDetails(accountId);
    }
}
