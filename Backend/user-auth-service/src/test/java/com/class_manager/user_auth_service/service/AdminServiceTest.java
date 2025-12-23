package com.class_manager.user_auth_service.service;

import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;

import com.class_manager.user_auth_service.model.entity.*;
import com.class_manager.user_auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ExcelImportService excelImportService;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void createTeachers_shouldSaveTeachers_whenEmailNotExist() throws IOException {
        TeacherDto teacher = new TeacherDto();
        teacher.setEmail("teacher@example.com");
        List<TeacherDto> teachers = List.of(teacher);

        MultipartFile file = mock(MultipartFile.class);

        when(excelImportService.parseTeacherExcel(file))
                .thenReturn(teachers);

        when(userRepository.findByEmail(teacher.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        adminService.createTeachers(file);

        verify(userRepository, times(1)).save(any());
        assert(teacher.getRole().equals(Role.TEACHER.name()));
    }

    @Test
    public void createStudents_shouldSaveStudents_whenEmailNotExist() throws IOException {
        StudentDto student = new StudentDto();
        student.setEmail("student@example.com");
        List<StudentDto> students = List.of(student);

        MultipartFile file = mock(MultipartFile.class);

        when(excelImportService.parseStudentExcel(file))
                .thenReturn(students);

        when(userRepository.findByEmail(student.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        adminService.createStudents(file, Niveau.CI1, Filiere.INFO);

        verify(userRepository, times(1)).save(any());
        assert(student.getRole().equals(Role.STUDENT.name()));
    }

    @Test
    void createTeachers_shouldSkipExistingEmail() throws IOException {
        TeacherDto teacher1 = new TeacherDto();
        teacher1.setEmail("exist@example.com");
        TeacherDto teacher2 = new TeacherDto();
        teacher2.setEmail("new@example.com");
        List<TeacherDto> teachers = List.of(teacher1, teacher2);
        MultipartFile file = mock(MultipartFile.class);


        when(excelImportService.parseTeacherExcel(file)).thenReturn(teachers);
        User existingUser = new Teacher();
        existingUser.setEmail("exist@example.com");
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        adminService.createTeachers(file);
        verify(userRepository, never()).save(argThat(u -> u.getEmail().equals("exist@example.com")));
        verify(userRepository, times(1)).save(argThat(u -> u.getEmail().equals("new@example.com")));
    }

}
