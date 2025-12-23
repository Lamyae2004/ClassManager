package com.class_manager.user_auth_service.Controller;


import com.class_manager.user_auth_service.model.dto.StudentResponseDTO;
import com.class_manager.user_auth_service.model.entity.Filiere;
import com.class_manager.user_auth_service.model.entity.Niveau;
import com.class_manager.user_auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/students")
@RequiredArgsConstructor
public class StudentController {

    private final UserService studentService;

    @GetMapping
    public List<StudentResponseDTO> getStudentsByClasse(
            @RequestParam Filiere filiere,
            @RequestParam Niveau niveau
    ) {
        return studentService.getStudentsByClasse(filiere, niveau)
                .stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }
}
