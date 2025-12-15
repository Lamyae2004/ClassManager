package com.class_manager.Gestion_des_absences.service;


import com.class_manager.Gestion_des_absences.client.EmploiDuTempsClient;
import com.class_manager.Gestion_des_absences.model.dto.EmploiDuTempsDTO;
import com.class_manager.Gestion_des_absences.model.dto.SeanceDetailsDTO;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeanceService {
/*
    private final SeanceRepository seanceRepository;
    private final EmploiDuTempsClient edtClient;

    public SeanceDetailsDTO getSeanceDetails(Long idSeance) {

        Seance seance = seanceRepository.findById(idSeance)
                .orElseThrow(() -> new RuntimeException("Seance introuvable"));

        EmploiDuTempsDTO edt = edtClient.getEmploiById(seance.getIdEdt());

        return new SeanceDetailsDTO(seance, edt);
    }*/
}
