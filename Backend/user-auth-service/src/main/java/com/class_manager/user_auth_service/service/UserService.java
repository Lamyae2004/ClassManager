package com.class_manager.user_auth_service.service;

import com.class_manager.user_auth_service.model.dto.AdminDto;
import com.class_manager.user_auth_service.model.dto.StudentDto;
import com.class_manager.user_auth_service.model.dto.TeacherDto;
import com.class_manager.user_auth_service.model.dto.UserMapper;
import com.class_manager.user_auth_service.model.entity.Filiere;
import com.class_manager.user_auth_service.model.entity.Niveau;
import com.class_manager.user_auth_service.model.entity.Student;
import com.class_manager.user_auth_service.repository.AdminRepository;
import com.class_manager.user_auth_service.repository.StudentRepository;
import com.class_manager.user_auth_service.repository.TeacherRepository;
import com.class_manager.user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final AdminRepository adminRepo;

    public List<StudentDto> getAllStudents() {
        return studentRepo.findAll()
                .stream()
                .map(student -> UserMapper.toStudentDto(student))
                .toList();
    }
    public List<StudentDto> getStudentsByNiveauAndFiliere(Niveau niveau, Filiere filiere){
        return studentRepo.findByNiveauAndFiliere(niveau,filiere)
                .stream()
                .map(student -> UserMapper.toStudentDto(student))
                .toList();
    }
    public List<TeacherDto> getAllTeachers() {
        return teacherRepo.findAll()
                .stream()
                .map(UserMapper::toTeacherDto)
                .toList();
    }
    public List<AdminDto> getAllAdmins() {
        return adminRepo.findAll()
                .stream()
                .map(UserMapper::toAdminDto)
                .toList();
    }
}
