package com.class_manager.Gestion_des_absences.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SeanceDTO {
    private Long id;
    private Long profId;
    private Long classeId;
    private Long creneauId;
    private String date;
    private List<AbsenceDTO> absences;
}
