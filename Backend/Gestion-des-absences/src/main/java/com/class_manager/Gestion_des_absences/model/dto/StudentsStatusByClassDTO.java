package com.class_manager.Gestion_des_absences.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentsStatusByClassDTO {
    private String classe;
    private String filiere;
    private long activeStudents;
    private long inactiveStudents;
}
