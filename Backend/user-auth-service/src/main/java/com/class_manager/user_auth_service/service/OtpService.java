package com.class_manager.user_auth_service.service;

import com.class_manager.user_auth_service.model.dto.MailBody;
import com.class_manager.user_auth_service.model.entity.ForgotPassword;
import com.class_manager.user_auth_service.model.entity.User;
import com.class_manager.user_auth_service.repository.ForgotPasswordRepository;
import com.class_manager.user_auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public Integer generateOtp(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }

    @Transactional
    public void sendOtpToEmail(String email, String subject, String messagePrefix) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        int otp = generateOtp();
        Date expirationTime = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        Optional<ForgotPassword> existingOtp = forgotPasswordRepository.findByUser(user);

        ForgotPassword forgotPassword;
        if (existingOtp.isPresent()) {
            forgotPassword = existingOtp.get();
            forgotPassword.setOtp(otp);
            forgotPassword.setExpirationTime(expirationTime);
        } else {

            forgotPassword = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(expirationTime)
                    .user(user)
                    .build();
        }

        // Envoyer l'email
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject(subject)
                .text(messagePrefix + otp)
                .build();
        emailService.sendSimpleMessage(mailBody);

        // Sauvegarder (création ou mise à jour)
        forgotPasswordRepository.save(forgotPassword);
    }
    public boolean verifyOtp(String email, int otp) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            throw new RuntimeException("OTP expired");
        }

        return true;
    }


}
