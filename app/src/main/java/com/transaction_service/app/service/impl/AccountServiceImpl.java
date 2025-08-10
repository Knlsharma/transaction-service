package com.transaction_service.app.service.impl;


import com.transaction_service.app.dto.AccountRequest;
import com.transaction_service.app.dto.AccountResponse;
import com.transaction_service.app.exception.AccountNotFoundException;
import com.transaction_service.app.exception.DuplicateDocumentException;
import com.transaction_service.app.model.Account;
import com.transaction_service.app.repository.AccountRepository;
import com.transaction_service.app.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Transactional
    public AccountResponse processAccountRequest(AccountRequest request) {

        if (accountRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new DuplicateDocumentException("Document number already exists: " + request.documentNumber());
        }

        Account account = new Account();
        account.setDocumentNumber(request.documentNumber());


        Account savedAccount = accountRepository.save(account);
//        log.info("Saved account with Id :  {}", savedAccount.getAccountId());
        return new AccountResponse(savedAccount.getAccountId(), savedAccount.getDocumentNumber());
    }


    public AccountResponse fetchAccountDetails(Long accountId) {
//        log.info("Get account with Id :  {}", accountId);
        Account account = getAccountById(accountId);
        return new AccountResponse(account.getAccountId(), account.getDocumentNumber());
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }


}
