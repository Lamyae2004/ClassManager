package com.class_manager.user_auth_service.auth;

import com.class_manager.user_auth_service.config.JwtService;
import com.class_manager.user_auth_service.exception.AccountNotActivatedException;
import com.class_manager.user_auth_service.model.entity.Role;
import com.class_manager.user_auth_service.model.entity.Student;
import com.class_manager.user_auth_service.model.entity.User;
import com.class_manager.user_auth_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void authenticate_shouldReturnToken_whenUserIsValidAndActivated() {
        User user = Student.builder()
                .id(1L)
                .email("test@mail.com")
                .isActivated(true)
                .role(Role.STUDENT)
                .build();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@mail.com", "password");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyMap(), eq(user)))
                .thenReturn("fake-jwt-token");

        AuthenticationResponse result =
                authenticationService.authenticate(authenticationRequest, response);

        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getToken());
    }

    @Test
    public void authenticate_shouldReturnException_whenUserAccountIsInActivated() {
        User user = Student.builder()
                .id(1L)
                .email("test@mail.com")
                .isActivated(false)
                .role(Role.STUDENT)
                .build();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@mail.com", "password");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));
        assertThrows(AccountNotActivatedException.class, () ->
                authenticationService.authenticate(authenticationRequest, response)
        );


    }

    @Test
    public void authenticate_shouldReturnException_whenUserNotFound() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@mail.com", "password");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                authenticationService.authenticate(authenticationRequest, response)
        );
    }

    @Test
    public void authenticate_shouldReturnException_whenPasswordIsWrong() {
        User user = Student.builder()
                .id(1L)
                .email("test@mail.com")
                .isActivated(true)
                .role(Role.STUDENT)
                .build();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@mail.com", "wrongPassword");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticate(authenticationRequest, response)
        );
    }

}