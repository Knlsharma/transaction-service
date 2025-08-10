package com.transaction_service.app.repository.impl;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;


@Repository
public class AccountRepositoryImpl implements IAccountCustomRepository
{

    private final EntityManager entityManager;

    public AccountRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(a) FROM Account a WHERE a.documentNumber = :docNum",
                        Long.class)
                .setParameter("docNum", documentNumber)
                .getSingleResult();

        return count > 0;
    }
}
