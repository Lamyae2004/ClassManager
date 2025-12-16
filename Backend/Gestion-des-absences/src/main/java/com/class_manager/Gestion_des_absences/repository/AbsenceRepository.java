package com.class_manager.Gestion_des_absences.repository;


import com.class_manager.Gestion_des_absences.model.entity.Absence;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository {
    List<Absence> findByIdSeance(Long idSeance);
    List<Absence> findByIdEtudiant(Long idEtudiant);
}
