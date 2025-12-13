package com.class_manager.Gestion_des_emplois.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImportRequest {
    private String classe;
    private String filiere;
    private String semestre;
    private String fileName;
    private List<EmploiImportDTO> emplois;
}