package com.transaction_service.app.repository.impl;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;


@Component
public interface IAccountCustomRepository {

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.documentNumber = ?1")
    boolean existsByDocumentNumber(String documentNumber);
}
