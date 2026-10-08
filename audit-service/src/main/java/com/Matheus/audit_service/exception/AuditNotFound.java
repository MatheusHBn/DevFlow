package com.Matheus.audit_service.exception;

public class AuditNotFound extends RuntimeException {
    public AuditNotFound(String message) {
        super(message);
    }
}
