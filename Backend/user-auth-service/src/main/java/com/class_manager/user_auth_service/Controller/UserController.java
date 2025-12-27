package com.class_manager.user_auth_service.Controller;

import com.class_manager.user_auth_service.config.JwtService;
import com.class_manager.user_auth_service.model.dto.AdminDto;
import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;
import com.class_manager.user_auth_service.model.dto.TeacherDtoMapper;
import com.class_manager.user_auth_service.model.entity.Teacher;
import com.class_manager.user_auth_service.model.entity.User;
import com.class_manager.user_auth_service.repository.UserRepository;
import com.class_manager.user_auth_service.repository.TeacherRepository;
import com.class_manager.user_auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final  UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public record UserResponse(Long id, String email, String firstname, String lastname, String role) {}
    private final TeacherRepository teacherRepository;


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserResponse response = new UserResponse(
                user.getId().longValue(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }





    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/students")
    public List<StudentDto> getAllStudents() {
        return userService.getAllStudents();
    }
    @GetMapping("/teachers")
    public List<TeacherDto> getAllTeachers() {
        return userService.getAllTeachers();
    }
    @GetMapping("/admins")
    public List<AdminDto> getAllAdmins() {
        return userService.getAllAdmins();
    }

    @GetMapping("/teachers/search")
    public TeacherDto getTeacherByFullName(
            @RequestParam String firstname,
            @RequestParam String lastname
    ) {
        Teacher teacher = teacherRepository
                .findByFirstnameIgnoreCaseAndLastnameIgnoreCase(firstname, lastname)
                .orElseThrow(() -> new RuntimeException("Prof non trouvé"));

        return TeacherDtoMapper.fromEntity(teacher);
    }

    @GetMapping("students/{studentId}")
    public StudentDto getStudentById(
            @PathVariable Long studentId
    ) {
        return userService.getStudentById(studentId);
    }

}
