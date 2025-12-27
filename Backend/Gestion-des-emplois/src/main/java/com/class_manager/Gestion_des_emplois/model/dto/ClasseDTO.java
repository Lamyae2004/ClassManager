package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClasseDTO {
    private Long id;
    private String nom;
    private String filiere;
}
