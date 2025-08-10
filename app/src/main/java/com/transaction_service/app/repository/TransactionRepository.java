package com.transaction_service.app.repository;

import com.transaction_service.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
