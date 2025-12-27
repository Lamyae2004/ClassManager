package com.class_manager.Gestion_des_absences.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassAbsenceRateDTO {
    private String classe;
    private String filiere;
    private double absenceRate;
}