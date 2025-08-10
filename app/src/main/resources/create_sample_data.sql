create table account
(
    account_id      bigint auto_increment
        primary key,
    document_number varchar(255) not null,
    constraint UKhr89i0j2q08s9tm3f6sek3hnq
        unique (document_number)
);

INSERT INTO account (document_number)
VALUES ('12345678900'),
       ('09876543211'),
       ('55555555555');

create table transaction
(
    transaction_id bigint auto_increment
        primary key,
    amount         decimal(10, 2) not null,
    event_date     timestamp(6)   not null,
    operation_type int            not null,
    account_id     bigint         not null,
    constraint FK6g20fcr3bhr6bihgy24rq1r1b
        foreign key (account_id) references account (account_id)
);

INSERT INTO transaction (account_id, operation_type, amount, event_date)
VALUES (1, 1, -50.00, '2023-01-01 10:00:00'),  -- CASH_PURCHASE
       (1, 1, -23.50, '2023-01-02 11:30:00'),  -- CASH_PURCHASE
       (1, 4, 100.00, '2023-01-03 09:15:00'),  -- PAYMENT
       (2, 2, -150.00, '2023-01-04 14:45:00'), -- INSTALLMENT_PURCHASE
       (2, 3, -200.00, '2023-01-05 16:20:00'), -- WITHDRAWAL
       (3, 4, 300.00, '2023-01-06 08:30:00'); -- PAYMENT


SELECT t.transaction_id,
       a.document_number,
       t.operation_type,
       CASE t.operation_type
           WHEN 1 THEN 'CASH_PURCHASE'
           WHEN 2 THEN 'INSTALLMENT_PURCHASE'
           WHEN 3 THEN 'WITHDRAWAL'
           WHEN 4 THEN 'PAYMENT'
           END AS operation_name,
       t.amount,
       t.event_date
FROM transaction t
         JOIN account a ON t.account_id = a.account_id;