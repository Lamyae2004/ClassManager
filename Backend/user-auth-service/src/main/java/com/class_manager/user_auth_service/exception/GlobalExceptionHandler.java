package com.class_manager.user_auth_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(Collections.singletonMap("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleServerError(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(Collections.singletonMap("error", "Erreur serveur : " + ex.getMessage()));
    }
    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotActivated(AccountNotActivatedException ex) {
        return ResponseEntity
                .status(403) // 403 Forbidden
                .body(Collections.singletonMap("error", ex.getMessage()));
    }
}
