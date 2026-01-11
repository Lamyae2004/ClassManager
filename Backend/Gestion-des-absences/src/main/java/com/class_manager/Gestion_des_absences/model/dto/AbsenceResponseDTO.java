package com.class_manager.Gestion_des_absences.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceResponseDTO {
    private Long id;
    private Long etudiantId;
    private Long seanceId;
    private boolean present;
    private boolean justifie;
    private String filePath;
    private String fileName;
    
    // Seance details
    private LocalDate date;
    private Long matiereId;
    private Long creneauId;
    private String heureDebut;
    private String heureFin;
    private String matiereNom;
}

