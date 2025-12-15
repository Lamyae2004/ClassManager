package com.class_manager.user_auth_service.Controller;

import com.class_manager.user_auth_service.config.JwtService;
import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;
import com.class_manager.user_auth_service.model.dto.UserMapper;
import com.class_manager.user_auth_service.model.entity.*;
import com.class_manager.user_auth_service.repository.UserRepository;
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
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelImportService excelService;
    private final JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createTeachers")
    public ResponseEntity<String> createTeachers(
            @RequestParam("file") MultipartFile file) throws IOException {

        List<TeacherDto> teachers = excelService.parseTeacherExcel(file);

        for(TeacherDto t: teachers){
            if(!repository.findByEmail(t.getEmail()).isPresent()){
                t.setRole(Role.TEACHER.name());
                t.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                repository.save(UserMapper.toTeacherEntity(t));
                var extraClaims = new HashMap<String,Object>();
                extraClaims.put("id", t.getId());
                extraClaims.put("role", t.getRole());
                String jwtToken = jwtService.generateToken(extraClaims,UserMapper.toEntity(t));
            }
        }

        return ResponseEntity.ok("Professeurs créés avec succès  ");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createStudents")
    public ResponseEntity<String> createStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("niveau") Niveau niveau,
            @RequestParam("filiere") Filiere filiere) throws IOException {

        List<StudentDto> students = excelService.parseStudentExcel(file);

        for(StudentDto s : students){
            if(!repository.findByEmail(s.getEmail()).isPresent()){
                s.setNiveau(niveau.name());
                s.setFiliere(filiere.name());
                s.setRole(Role.STUDENT.name());
                s.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                repository.save(UserMapper.toStudentEntity(s));
                var extraClaims = new HashMap<String,Object>();
                extraClaims.put("id", s.getId());
                extraClaims.put("role", s.getRole());
                String jwtToken = jwtService.generateToken(extraClaims,UserMapper.toEntity(s));
            }
        }

        return ResponseEntity.ok("Étudiants créés avec succès pour " + niveau + " " + filiere);
    }


}
