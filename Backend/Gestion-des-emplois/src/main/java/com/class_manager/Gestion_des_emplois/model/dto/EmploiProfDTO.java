package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploiProfDTO {
    private Long id;
    private String jour;
    private String creneauDebut;
    private String creneauFin;
    private String matiereNom;
    private String classeNom;
    private String filiere;
    private String salleNom;
}