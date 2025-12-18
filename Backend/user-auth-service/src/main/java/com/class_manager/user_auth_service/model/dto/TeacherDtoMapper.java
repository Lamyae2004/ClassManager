package com.class_manager.user_auth_service.model.dto;

import com.class_manager.user_auth_service.model.entity.Teacher;

public class TeacherDtoMapper {
    public static TeacherDto fromEntity(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .firstname(teacher.getFirstname())
                .lastname(teacher.getLastname())
                .email(teacher.getEmail())
                .teacherCode(teacher.getTeacherCode())
                .speciality(teacher.getSpeciality())
                .build();
    }
}
