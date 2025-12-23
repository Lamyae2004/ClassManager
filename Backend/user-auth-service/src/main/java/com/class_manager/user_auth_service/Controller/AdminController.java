package com.class_manager.user_auth_service.Controller;

import com.class_manager.user_auth_service.config.JwtService;
import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;
import com.class_manager.user_auth_service.model.dto.UserMapper;
import com.class_manager.user_auth_service.model.entity.*;
import com.class_manager.user_auth_service.repository.UserRepository;
import com.class_manager.user_auth_service.service.AdminService;
import com.class_manager.user_auth_service.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createTeachers")
    public ResponseEntity<String> createTeachers(@RequestParam("file") MultipartFile file) throws IOException {
        adminService.createTeachers(file);
        return ResponseEntity.ok("Professeurs créés avec succès  ");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createStudents")
    public ResponseEntity<String> createStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("niveau") Niveau niveau,
            @RequestParam("filiere") Filiere filiere) throws IOException {
        adminService.createStudents(file,niveau,filiere);
        return ResponseEntity.ok("Étudiants créés avec succès pour " + niveau + " " + filiere);
    }


}
