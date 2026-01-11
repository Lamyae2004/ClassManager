package com.class_manager.user_auth_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentClasseDTO {
    private String niveau;
    private String filiere;
    private String classe;
}