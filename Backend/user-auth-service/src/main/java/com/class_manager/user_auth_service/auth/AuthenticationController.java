package com.class_manager.user_auth_service.auth;

import com.class_manager.user_auth_service.model.dto.ChangePassword;
import com.class_manager.user_auth_service.model.dto.UserDto;
import com.class_manager.user_auth_service.model.dto.UserMapper;
import com.class_manager.user_auth_service.model.entity.User;
import com.class_manager.user_auth_service.repository.UserRepository;
import com.class_manager.user_auth_service.service.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService service;

//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse>register(@RequestBody RegisterRequest request){
//     return ResponseEntity.ok(service.register(request));
//    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse>authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response){
    return  ResponseEntity.ok(service.authenticate(request,response));
    }

    @PostMapping("/forgot-password/request/{email}")
    public ResponseEntity<?> requestForgotPassword(@PathVariable String email) {
        otpService.sendOtpToEmail(
                email,
                "OTP for password reset",
                "Your OTP for resetting password is: "
        );
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/forgot-password/verify/{email}/{otp}")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @PathVariable String email,
            @PathVariable int otp
    ) {
        otpService.verifyOtp(email, otp);
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/forgot-password/change/{email}")
    public ResponseEntity<?> changePassword(
            @PathVariable String email,
            @RequestBody ChangePassword changePassword
    ) {
        if (!changePassword.password().equals(changePassword.repeatPassword())) {
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        String encoded = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encoded);

        return ResponseEntity.ok("Password changed successfully");
    }
    @PostMapping("/validate-account/request/{email}")
    public ResponseEntity<?> requestValidateAccount(@PathVariable String email) {
        UserDto user = UserMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")));
        if(user.isActivated()){
            return ResponseEntity.ok("Account already activated");
        }
        otpService.sendOtpToEmail(
                email,
                "Account activation OTP",
                "Your OTP to activate your account: "
        );
        return ResponseEntity.ok("OTP sent to email");
    }
    @PostMapping("/validate-account/verify/{email}/{otp}")
    public ResponseEntity<?> verifyAccountActivation(
            @PathVariable String email,
            @PathVariable int otp
    ) {
        otpService.verifyOtp(email, otp);
        return ResponseEntity.ok("OTP verified");
    }
    @PostMapping("/validate-account/set-password/{email}")
    public ResponseEntity<?> activateAccount(
            @PathVariable String email,
            @RequestBody ChangePassword changePassword
    ) {
        if (!changePassword.password().equals(changePassword.repeatPassword())) {
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        UserDto user = UserMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")));

        user.setPassword(passwordEncoder.encode(changePassword.password()));
        user.setActivated(true);
        userRepository.save(UserMapper.toEntity(user));
        return ResponseEntity.ok("Account Activated successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        service.clearJwtCookie(response);
        return ResponseEntity.ok().build();
    }



}
