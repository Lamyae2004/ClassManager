package com.class_manager.Gestion_des_absences.model.dto;

import lombok.Data;

@Data
public class AbsenceDTO {
    private Long id;
    private Long idEtudiant;
    private Long idSeance;
    private boolean justifie;
    private String justificatif;
}
