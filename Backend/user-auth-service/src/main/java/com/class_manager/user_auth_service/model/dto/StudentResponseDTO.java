package com.class_manager.user_auth_service.model.dto;


import com.class_manager.user_auth_service.model.entity.Student;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String apogeeNumber;

    public static StudentResponseDTO fromEntity(Student s) {
        return StudentResponseDTO.builder()
                .id(s.getId())
                .firstname(s.getFirstname())
                .lastname(s.getLastname())
                .apogeeNumber(s.getApogeeNumber())
                .build();
    }
}
