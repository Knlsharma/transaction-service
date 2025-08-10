package com.transaction_service.app.service.impl;

import com.transaction_service.app.dto.TransactionRequest;
import com.transaction_service.app.dto.TransactionResponse;
import com.transaction_service.app.enums.OperationType;
import com.transaction_service.app.model.Account;
import com.transaction_service.app.model.Transaction;
import com.transaction_service.app.repository.AccountRepository;
import com.transaction_service.app.repository.TransactionRepository;
import com.transaction_service.app.service.AccountService;
import com.transaction_service.app.service.TransactionService;
import com.transaction_service.app.utils.AppUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public TransactionResponse createTransaction(TransactionRequest request) {
        Account account = accountService.getAccountById(request.accountId());

        OperationType operationType = AppUtil.parseOperationType(request.operationTypeId());

        BigDecimal amount = AppUtil.validateAndConvertAmount(request.amount(), operationType);

        Transaction transaction = createTransactionEntity(account, operationType, amount);

        return saveTransaction(transaction);
    }

    private Transaction createTransactionEntity(Account account, OperationType operationType, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperationType(operationType);
        transaction.setAmount(amount);
        transaction.setEventDate(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        return transaction;
    }

    private TransactionResponse saveTransaction(Transaction transaction) {
        Transaction saved = transactionRepository.save(transaction);
//        log.info("Created transaction ID: {} for account ID: {}",
//                saved.getTransactionId(), saved.getAccount().getAccountId());

        return new TransactionResponse(
                saved.getTransactionId(),
                saved.getAccount().getAccountId(),
                saved.getOperationType().getId(),
                saved.getAmount(),
                saved.getEventDate()
        );
    }
}
