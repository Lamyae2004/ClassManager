package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.Data;

@Data
public class EmploiCellUpdateDTO {
    private String matiere;
    private String prof;
    private String salle;
    private String creneau;  // format "08:30-10:30"
    private String jour;
}