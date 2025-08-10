package com.transaction_service.app.exception;

public class DuplicateDocumentException extends RuntimeException {
    public DuplicateDocumentException(String msg) {
        super(msg);
    }
}