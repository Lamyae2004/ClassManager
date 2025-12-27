package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class EmploiAvecProfResponse {
    private EmploiDuTempsDTO emploi;
    private String profNom;
}

