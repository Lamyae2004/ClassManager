package com.class_manager.Gestion_des_absences.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmploiDuTempsDTO {
    private Long id;
    private String jour;
    //private ClasseDTO classe;
    private MatiereDTO matiere;
    private SalleDTO salle;
    private CreneauDTO creneau;
    private ProfDTO prof;
}
