package com.class_manager.user_auth_service.auth;

import com.class_manager.user_auth_service.model.dto.AdminDto;
import com.class_manager.user_auth_service.model.dto.UserDto;
import com.class_manager.user_auth_service.model.dto.UserMapper;
import com.class_manager.user_auth_service.model.entity.Role;
import com.class_manager.user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder ;

    @Bean
    CommandLineRunner initAdmin(){
        return args->{
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);

            if (!adminExists) {
                String randomPassword = UUID.randomUUID().toString().substring(0, 8);

                AdminDto admin = AdminDto.builder()
                        .firstname("Sara")
                        .lastname("BGHIGH")
                        .email("sara.bghigh@uit.ac.ma")
                        .password(passwordEncoder.encode(randomPassword))
                        .role(Role.ADMIN.name())
                        .isActivated(false)
                        .build();

                userRepository.save(UserMapper.toAdminEntity(admin));
                System.out.println("Admin créé automatiquement");
            }
        };
    }
}
