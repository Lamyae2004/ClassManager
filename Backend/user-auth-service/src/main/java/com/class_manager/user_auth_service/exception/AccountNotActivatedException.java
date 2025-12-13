package com.class_manager.user_auth_service.exception;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException(String message) {
        super(message);
    }
}