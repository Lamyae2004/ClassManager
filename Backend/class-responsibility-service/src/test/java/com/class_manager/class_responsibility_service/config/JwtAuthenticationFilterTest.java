package com.class_manager.class_responsibility_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String SECRET_KEY = "23ff91809d4e7aa827303ffcbebc2f03cd708903d700454e7c11710aa0f91349";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidTokenFromCookie() throws Exception {
        // Given
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", token)};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(jwtService.extractClaim(eq(token), any())).thenReturn("ADMIN");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).isTokenValid(token);
        verify(jwtService, times(1)).extractUsername(token);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidTokenFromAuthorizationHeader() throws Exception {
        // Given
        String token = createTestToken("testuser", "TEACHER", new Date(System.currentTimeMillis() + 3600000));
        String authHeader = "Bearer " + token;

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(jwtService.extractClaim(eq(token), any())).thenReturn("TEACHER");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).isTokenValid(token);
        verify(jwtService, times(1)).extractUsername(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid.token";
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtService.isTokenValid(invalidToken)).thenReturn(false);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(stringWriter.toString().contains("Token invalide ou absent"));
    }

    @Test
    void testDoFilterInternal_NoToken() throws Exception {
        // Given
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(stringWriter.toString().contains("Token invalide ou absent"));
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderWithoutBearer() throws Exception {
        // Given - Test branch where authHeader != null but doesn't start with "Bearer "
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token123");
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(stringWriter.toString().contains("Token invalide ou absent"));
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderNull() throws Exception {
        // Given - Test branch where authHeader is null (different from empty)
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilterInternal_TokenFromCookieTakesPrecedence() throws Exception {
        // Given
        String cookieToken = createTestToken("cookieuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        String headerToken = createTestToken("headeruser", "TEACHER", new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", cookieToken)};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + headerToken);
        when(jwtService.isTokenValid(cookieToken)).thenReturn(true);
        when(jwtService.extractUsername(cookieToken)).thenReturn("cookieuser");
        when(jwtService.extractClaim(eq(cookieToken), any())).thenReturn("ADMIN");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).isTokenValid(cookieToken);
        verify(jwtService, never()).isTokenValid(headerToken);
        verify(jwtService, times(1)).extractUsername(cookieToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyCookiesArray() throws Exception {
        // Given
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        String authHeader = "Bearer " + token;

        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(jwtService.extractClaim(eq(token), any())).thenReturn("ADMIN");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).isTokenValid(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_CookieWithoutTokenName() throws Exception {
        // Given
        String token = createTestToken("testuser", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        String authHeader = "Bearer " + token;
        Cookie[] cookies = {new Cookie("otherCookie", "value")};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(jwtService.extractClaim(eq(token), any())).thenReturn("ADMIN");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).isTokenValid(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testShouldNotFilter_PublicPath() {
        // Given
        when(request.getServletPath()).thenReturn("/public/test");

        // When
        boolean shouldNotFilter = filter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void testShouldNotFilter_NonPublicPath() {
        // Given
        when(request.getServletPath()).thenReturn("/api/responsible/history");

        // When
        boolean shouldNotFilter = filter.shouldNotFilter(request);

        // Then
        assertFalse(shouldNotFilter);
    }

    @Test
    void testShouldNotFilter_PublicPathWithSubPath() {
        // Given
        when(request.getServletPath()).thenReturn("/public/auth/login");

        // When
        boolean shouldNotFilter = filter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter);
    }

    @Test
    void testDoFilterInternal_DifferentRoles() throws Exception {
        // Given
        String adminToken = createTestToken("admin", "ADMIN", new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", adminToken)};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtService.isTokenValid(adminToken)).thenReturn(true);
        when(jwtService.extractUsername(adminToken)).thenReturn("admin");
        when(jwtService.extractClaim(eq(adminToken), any())).thenReturn("ADMIN");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("ROLE_ADMIN", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderNotStartingWithBearer() throws Exception {
        // Given - Test branch where authHeader != null but doesn't start with "Bearer "
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic token123"); // Not "Bearer "
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertTrue(stringWriter.toString().contains("Token invalide ou absent"));
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderEmptyString() throws Exception {
        // Given - Test branch where authHeader is empty string
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getCookies()).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(""); // Empty string
        when(response.getWriter()).thenReturn(printWriter);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilterInternal_RoleExtraction() throws Exception {
        // Given - Test role extraction from token
        String token = createTestToken("testuser", "TEACHER", new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", token)};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(jwtService.extractClaim(eq(token), any())).thenReturn("TEACHER");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService, times(1)).extractClaim(eq(token), any());
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("ROLE_TEACHER", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testDoFilterInternal_LambdaExecution() throws Exception {
        // Given - Test that the lambda claims -> claims.get("role", String.class) is actually executed
        String token = createTestToken("testuser", "STUDENT", new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", token)};

        // Use ArgumentCaptor to capture the lambda function
        ArgumentCaptor<Function<Claims, String>> lambdaCaptor = ArgumentCaptor.forClass(Function.class);

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        
        // Use doAnswer to actually execute the lambda
        when(jwtService.extractClaim(eq(token), lambdaCaptor.capture())).thenAnswer(invocation -> {
            Function<Claims, String> lambda = invocation.getArgument(1);
            // Create a mock Claims object to test the lambda
            Claims mockClaims = mock(Claims.class);
            when(mockClaims.get("role", String.class)).thenReturn("STUDENT");
            return lambda.apply(mockClaims);
        });

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then - Verify the lambda was captured and executed
        verify(jwtService, times(1)).extractClaim(eq(token), any());
        Function<Claims, String> capturedLambda = lambdaCaptor.getValue();
        assertNotNull(capturedLambda);
        
        // Test the lambda directly
        Claims testClaims = mock(Claims.class);
        when(testClaims.get("role", String.class)).thenReturn("STUDENT");
        String result = capturedLambda.apply(testClaims);
        assertEquals("STUDENT", result);
        
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("ROLE_STUDENT", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testDoFilterInternal_LambdaWithNullRole() throws Exception {
        // Given - Test lambda execution when role is null
        String token = createTestToken("testuser", null, new Date(System.currentTimeMillis() + 3600000));
        Cookie[] cookies = {new Cookie("token", token)};

        ArgumentCaptor<Function<Claims, String>> lambdaCaptor = ArgumentCaptor.forClass(Function.class);

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        
        // Execute the lambda with null role
        when(jwtService.extractClaim(eq(token), lambdaCaptor.capture())).thenAnswer(invocation -> {
            Function<Claims, String> lambda = invocation.getArgument(1);
            Claims mockClaims = mock(Claims.class);
            when(mockClaims.get("role", String.class)).thenReturn(null);
            return lambda.apply(mockClaims);
        });

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then - Verify lambda was executed and handled null role
        verify(jwtService, times(1)).extractClaim(eq(token), any());
        Function<Claims, String> capturedLambda = lambdaCaptor.getValue();
        assertNotNull(capturedLambda);
        
        // Test the lambda with null
        Claims testClaims = mock(Claims.class);
        when(testClaims.get("role", String.class)).thenReturn(null);
        String result = capturedLambda.apply(testClaims);
        assertNull(result);
        
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        // When role is null, authority will be "ROLE_null"
        assertEquals("ROLE_null", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
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
