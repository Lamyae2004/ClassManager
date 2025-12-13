package com.class_manager.user_auth_service.auth;

import com.class_manager.user_auth_service.model.entity.Filiere;
import com.class_manager.user_auth_service.model.entity.Niveau;
import com.class_manager.user_auth_service.model.entity.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;      // ADMIN, STUDENT, TEACHER

    // Champs spécifiques student
    private String apogeeNumber;
    private Filiere filiere;
    private Niveau niveau;

    // Champs spécifiques teacher
    private String teacherCode;
    private String speciality;


}
