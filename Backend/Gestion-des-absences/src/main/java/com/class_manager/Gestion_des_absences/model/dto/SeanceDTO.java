package com.class_manager.Gestion_des_absences.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SeanceDTO {
    private Long id;
    private Long idEdt;
    private LocalDate dateSeance;
}
