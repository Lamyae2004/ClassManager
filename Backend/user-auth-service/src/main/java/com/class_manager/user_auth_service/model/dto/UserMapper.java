package com.class_manager.user_auth_service.model.dto;

import com.class_manager.user_auth_service.model.entity.*;

public class UserMapper {
    public static UserDto toDto(User user){
        if(user instanceof Admin){
            return toAdminDto((Admin)user);
        }
        if(user instanceof Student){
            return toStudentDto((Student)user);
        }
        if(user instanceof Teacher){
            return toTeacherDto((Teacher)user);
        }
        return null;
    }
    public static User toEntity(UserDto dto){
        if(dto instanceof AdminDto){
            return toAdminEntity((AdminDto)dto);
        }
        if(dto instanceof StudentDto){
            return toStudentEntity((StudentDto)dto);
        }
        if(dto instanceof TeacherDto){
            return toTeacherEntity((TeacherDto)dto);
        }
        return null;
    }

    public static Teacher toTeacherEntity(TeacherDto dto) {
        return Teacher.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.valueOf(dto.getRole()))
                .teacherCode(dto.getTeacherCode())
                .speciality(dto.getSpeciality())
                .isActivated(dto.isActivated())
                .build();
    }

    public static Student toStudentEntity(StudentDto dto) {
        return Student.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.valueOf(dto.getRole()))
                .apogeeNumber(dto.getApogeeNumber())
                .filiere(Filiere.valueOf(dto.getFiliere()))
                .niveau(Niveau.valueOf(dto.getNiveau()))
                .isActivated(dto.isActivated())
                .build();
    }

    public static Admin toAdminEntity(AdminDto dto) {
        return Admin.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.valueOf(dto.getRole()))
                .isActivated(dto.isActivated())
                .build();
    }

    public static AdminDto toAdminDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .firstname(admin.getFirstname())
                .lastname(admin.getLastname())
                .email(admin.getEmail())
                .password(admin.getPassword())
                .role(admin.getRole().name())
                .isActivated(admin.isActivated())
                .build();
    }

    public static TeacherDto toTeacherDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .firstname(teacher.getFirstname())
                .lastname(teacher.getLastname())
                .email(teacher.getEmail())
                .password(teacher.getPassword())
                .role(teacher.getRole().name())
                .teacherCode(teacher.getTeacherCode())
                .speciality(teacher.getSpeciality())
                .isActivated(teacher.isActivated())
                .build();
    }

    public static StudentDto toStudentDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .email(student.getEmail())
                .password(student.getPassword())
                .role(student.getRole().name())
                .apogeeNumber(student.getApogeeNumber())
                .filiere(student.getFiliere().name())
                .niveau(student.getNiveau().name())
                .isActivated(student.isActivated())
                .build();
    }
}