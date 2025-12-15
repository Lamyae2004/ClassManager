package com.class_manager.Gestion_des_absences.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassDTO {
    private Long id;
    private String nom;
    private String filiere;
}
