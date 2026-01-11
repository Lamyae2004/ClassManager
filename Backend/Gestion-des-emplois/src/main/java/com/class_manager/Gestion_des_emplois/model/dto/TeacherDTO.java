package com.class_manager.Gestion_des_emplois.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String speciality;
}
