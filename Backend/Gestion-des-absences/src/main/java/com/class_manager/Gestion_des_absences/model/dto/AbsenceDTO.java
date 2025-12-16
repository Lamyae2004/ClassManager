package com.class_manager.Gestion_des_absences.model.dto;

import com.class_manager.Gestion_des_absences.model.entity.Absence;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbsenceDTO {
    private Absence absence;
    private Seance seance;
    private EmploiDuTempsDTO emploi;
    private EtudiantDTO etudiant;
}
