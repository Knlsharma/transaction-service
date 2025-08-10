package com.transaction_service.app.repository;

import com.transaction_service.app.model.Account;
import com.transaction_service.app.repository.impl.IAccountCustomRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public interface AccountRepository extends JpaRepository<Account, Long>, IAccountCustomRepository {
    // for jpa + custom implementation

    @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findById(Long accountId);
}
