package com.class_manager.Gestion_des_emplois.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploiDuTempsDTO {
    private Long id;

    private Long classeId;
    private String classeNom;

    private Long matiereId;
    private String matiereNom;

    private Long salleId;
    private String salleNom;

    private Long creneauId;
    private String creneauDebut;
    private String creneauFin;

    private Long profId;

    private String jour;

}
