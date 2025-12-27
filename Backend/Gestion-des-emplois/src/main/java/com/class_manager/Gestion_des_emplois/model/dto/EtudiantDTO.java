package com.class_manager.Gestion_des_emplois.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EtudiantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String apogeeNumber;
    private String filiere;
    private String niveau;
}
