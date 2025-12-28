package com.class_manager.Gestion_des_emplois.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantActionDTO {
    private Long id;
    private String nom;
    private String prenom;
    @JsonProperty("email")
    private String email;
    private String apogeeNumber;
    private String filiere;
    private String niveau;
}