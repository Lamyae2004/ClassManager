package com.class_manager.user_auth_service.auth;

import com.class_manager.user_auth_service.config.JwtService;
import com.class_manager.user_auth_service.exception.AccountNotActivatedException;
import com.class_manager.user_auth_service.model.entity.*;
import com.class_manager.user_auth_service.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
//    public AuthenticationResponse register(RegisterRequest request) {
//      Optional<User> existingUser = repository.findByEmail(request.getEmail());
//      if(existingUser.isPresent()){
//          throw new IllegalArgumentException("Email déjà existant !");
//      }
//      User user ;
//      switch (request.getRole()){
//          case STUDENT:
//              user = Student.builder()
//                      .firstname(request.getFirstname())
//                      .lastname(request.getLastname())
//                      .email(request.getEmail())
//                      .password(passwordEncoder.encode(request.getPassword()))
//                      .role(Role.STUDENT)
//                      .apogeeNumber(request.getApogeeNumber())
//                      .filiere(request.getFiliere())
//                      .niveau(request.getNiveau())
//                      .build();
//              break;
//          case TEACHER:
//              user = Teacher.builder()
//                      .firstname(request.getFirstname())
//                      .lastname(request.getLastname())
//                      .email(request.getEmail())
//                      .password(passwordEncoder.encode(request.getPassword()))
//                      .role(Role.TEACHER)
//                      .teacherCode(request.getTeacherCode())
//                      .speciality(request.getSpeciality())
//                      .build();
//              break;
//
//          case ADMIN:
//              user = Admin.builder()
//                      .firstname(request.getFirstname())
//                      .lastname(request.getLastname())
//                      .email(request.getEmail())
//                      .password(passwordEncoder.encode(request.getPassword()))
//                      .role(Role.ADMIN)
//                      .build();
//              break;
//          default:
//              throw new IllegalArgumentException("Invalid role");
//      }
//      repository.save(user);
//      HashMap<String,Object> extraClaims =new HashMap<>();
//      extraClaims.put("id",user.getId());
//      extraClaims.put("role",user.getRole());
//      var jwtToken = jwtService.generateToken(extraClaims,user);
//       return AuthenticationResponse.builder()
//               .token(jwtToken)
//               .build();
//    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        if (!user.isActivated()) {
            throw new AccountNotActivatedException("Veuillez activer votre compte avant de vous connecter");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getRole());


        String jwtToken = jwtService.generateToken(extraClaims, user);

        // Créer le cookie
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
