package com.class_manager.Gestion_des_emplois.model.dto;

import com.class_manager.Gestion_des_emplois.model.entity.Semestre;
import lombok.Data;

@Data
public class EmploiImportDTO {
    private String jour;
    private String creneau;  // format "08:30-10:30"
    private String matiere;
    private String prof;
    private String salle;
    private String semestre;
}
