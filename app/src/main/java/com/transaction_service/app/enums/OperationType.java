package com.transaction_service.app.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OperationType {

    CASH_PURCHASE(1, -1),
    INSTALLMENT_PURCHASE(2, -1),
    WITHDRAWAL(3, -1),
    PAYMENT(4, 1);

    private final int id;
    private final int sign;

    private static final Map<Integer, OperationType> ID_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(OperationType::getId, Function.identity()));

    OperationType(int id, int sign) {
        this.id = id;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public int getSign() {
        return sign;
    }

    public static OperationType fromId(int id) {
        OperationType type = ID_MAP.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Invalid operation Id: " + id);
        }
        return type;
    }

}
