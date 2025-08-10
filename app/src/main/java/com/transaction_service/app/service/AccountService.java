package com.transaction_service.app.service;

import com.transaction_service.app.dto.AccountRequest;
import com.transaction_service.app.dto.AccountResponse;
import com.transaction_service.app.model.Account;
import org.springframework.stereotype.Component;

@Component
public interface AccountService {

    AccountResponse processAccountRequest(AccountRequest request);

    AccountResponse fetchAccountDetails(Long accountId);

    Account getAccountById(Long accountId);
}
