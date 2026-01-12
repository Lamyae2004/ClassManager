package com.class_manager.class_responsibility_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleIllegalArgument_Success() {
        // Given
        String errorMessage = "Aucune classe trouvée pour ce niveau et cette filière";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalArgument(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void testHandleIllegalArgument_EmptyMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalArgument(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("", response.getBody().get("error"));
    }

    @Test
    void testHandleIllegalArgument_NullMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException((String) null);

        // When/Then - Map.of doesn't accept null values, so this will throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            exceptionHandler.handleIllegalArgument(exception);
        });
    }

    @Test
    void testHandleGeneric_Success() {
        // Given
        RuntimeException exception = new RuntimeException("Some internal error");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Erreur serveur", response.getBody().get("error"));
    }

    @Test
    void testHandleGeneric_NullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException();

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erreur serveur", response.getBody().get("error"));
    }

    @Test
    void testHandleGeneric_IllegalStateException() {
        // Given
        IllegalStateException exception = new IllegalStateException("State error");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erreur serveur", response.getBody().get("error"));
    }

    @Test
    void testHandleGeneric_WithCause() {
        // Given
        Exception cause = new IllegalArgumentException("Cause");
        RuntimeException exception = new RuntimeException("Wrapper", cause);

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erreur serveur", response.getBody().get("error"));
    }
}
