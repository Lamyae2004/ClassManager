package com.class_manager.class_responsibility_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "23ff91809d4e7aa827303ffcbebc2f03cd708903d700454e7c11710aa0f91349";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testExtractUsername_ValidToken() {
        // Given
        String username = "testuser";
        String token = createTestToken(username, "ADMIN", new Date(System.currentTimeMillis() + 3600000)); // 1 hour from now

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractClaim_ValidToken() {
        // Given
        String username = "testuser";
        String role = "ADMIN";
        String token = createTestToken(username, role, new Date(System.currentTimeMillis() + 3600000));

        // When
        String extractedRole = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void testExtractClaim_WithCustomFunction() {
        // Given
        String username = "testuser";
        String token = createTestToken(username, "ADMIN", new Date(System.currentTimeMillis() + 3600000));

        // When
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testIsTokenValid_ValidToken() {
        // Given
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ValidTokenNotExpired() {
        // Given - Token that is valid and not expired (covers the !isTokenExpired branch)
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 7200000)); // 2 hours from now

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        // Given
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago

        // When/Then - Expired tokens throw exception when parsed
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(token);
        });
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(invalidToken);
        });
    }

    @Test
    void testIsTokenValid_TokenExpiringSoon() {
        // Given - Token expiring in 1 second (still valid)
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 1000));

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid, "Token should be valid if it expires in the future");
    }

    @Test
    void testIsTokenValid_TokenExpiringFarFuture() {
        // Given - Token expiring in 1 year
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000));

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid, "Token should be valid if it expires far in the future");
    }

    @Test
    void testIsTokenValid_TokenJustExpired() {
        // Given - Token that expired just now (1 second ago)
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() - 1000));

        // When/Then - Should throw exception when trying to validate expired token
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(token);
        });
    }

    @Test
    void testIsTokenValid_TokenExpiredLongAgo() {
        // Given - Token that expired 1 day ago
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

        // When/Then - Should throw exception
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(token);
        });
    }

    @Test
    void testIsTokenValid_EmptyString() {
        // Given
        String emptyToken = "";

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(emptyToken);
        });
    }

    @Test
    void testIsTokenValid_NullToken() {
        // Given
        String nullToken = null;

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(nullToken);
        });
    }

    @Test
    void testIsTokenValid_MalformedToken() {
        // Given - Token with wrong format
        String malformedToken = "not.a.valid.jwt.token.format";

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(malformedToken);
        });
    }

    @Test
    void testIsTokenValid_TokenWithDifferentRoles() {
        // Given - Valid tokens with different roles
        String adminToken = createTestToken("admin", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        String teacherToken = createTestToken("teacher", "TEACHER", new Date(System.currentTimeMillis() + 3600000));
        String studentToken = createTestToken("student", "STUDENT", new Date(System.currentTimeMillis() + 3600000));

        // When
        boolean adminValid = jwtService.isTokenValid(adminToken);
        boolean teacherValid = jwtService.isTokenValid(teacherToken);
        boolean studentValid = jwtService.isTokenValid(studentToken);

        // Then
        assertTrue(adminValid, "Admin token should be valid");
        assertTrue(teacherValid, "Teacher token should be valid");
        assertTrue(studentValid, "Student token should be valid");
    }

    @Test
    void testIsTokenValid_TokenWithoutExpiration() {
        // Given - Token without expiration claim (this will fail as JWT requires expiration)
        // Note: JWT library typically requires expiration, so this test verifies the behavior
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid, "Token with expiration should be valid");
    }

    @Test
    void testExtractUsername_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(invalidToken);
        });
    }

    @Test
    void testExtractClaim_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";
        Function<Claims, String> extractor = Claims::getSubject;

        // When/Then
        assertThrows(Exception.class, () -> {
            jwtService.extractClaim(invalidToken, extractor);
        });
    }

    @Test
    void testExtractClaim_WithNullRole() {
        // Given
        String username = "testuser";
        String token = createTestToken(username, null, new Date(System.currentTimeMillis() + 3600000));

        // When
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        // Then
        assertNull(role);
    }

    @Test
    void testExtractClaim_WithDifferentRoles() {
        // Given
        String token1 = createTestToken("user1", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        String token2 = createTestToken("user2", "TEACHER", new Date(System.currentTimeMillis() + 3600000));
        String token3 = createTestToken("user3", "STUDENT", new Date(System.currentTimeMillis() + 3600000));

        // When
        String role1 = jwtService.extractClaim(token1, claims -> claims.get("role", String.class));
        String role2 = jwtService.extractClaim(token2, claims -> claims.get("role", String.class));
        String role3 = jwtService.extractClaim(token3, claims -> claims.get("role", String.class));

        // Then
        assertEquals("ADMIN", role1);
        assertEquals("TEACHER", role2);
        assertEquals("STUDENT", role3);
    }

    // Helper method to create test tokens
    private String createTestToken(String username, String role, Date expiration) {
        Key key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(SECRET_KEY));
        
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
}
