package com.class_manager.class_responsibility_service.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassDTO {
    private Long id;
    private String nom;
    private String filiere;
}
