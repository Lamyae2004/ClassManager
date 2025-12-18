package com.class_manager.Gestion_des_emplois.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploiDuTempsDTO {
    private Long id;          // id de l'emploi
    private Long creneauId;
    private String heureDebut;
    private String heureFin;
    private String matiereNom;
    private String salleNom;
    private Long profId;
}
