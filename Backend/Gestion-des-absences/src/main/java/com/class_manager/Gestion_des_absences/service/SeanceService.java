package com.class_manager.Gestion_des_absences.service;


import com.class_manager.Gestion_des_absences.model.dto.SeanceDTO;
import com.class_manager.Gestion_des_absences.model.entity.Absence;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceService {
    private final SeanceRepository seanceRepository;

    public Seance saveSeanceWithAbsences(SeanceDTO request) {
        Seance seance = new Seance();
        seance.setProfId(request.getProfId());
        seance.setClasseId(request.getClasseId());
        seance.setCreneauId(request.getCreneauId());
        seance.setDate(LocalDate.parse(request.getDate()));

        List<Absence> absences = request.getAbsences().stream().map(a -> {
            Absence absence = new Absence();
            absence.setEtudiantId(a.getEtudiantId());
            absence.setPresent(a.isPresent());
            absence.setSeance(seance);
            return absence;
        }).toList();

        seance.setAbsences(absences);

        return seanceRepository.save(seance);
    }


}
