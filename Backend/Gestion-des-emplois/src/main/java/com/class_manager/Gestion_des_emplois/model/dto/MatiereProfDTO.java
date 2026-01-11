package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class MatiereProfDTO {

    private Long matiereId;
    private String matiere;
    private String profNom;
    private String profPrenom;
}