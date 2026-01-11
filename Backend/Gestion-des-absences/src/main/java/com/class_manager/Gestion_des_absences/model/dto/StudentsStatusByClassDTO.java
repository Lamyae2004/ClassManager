package com.class_manager.Gestion_des_absences.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentsStatusByClassDTO {
    private String classe;
    private String filiere;
    private long activeStudents;
    private long inactiveStudents;
}
