package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {
    List<EmploiDuTemps> findByClasse_Nom(String nom);
    List<EmploiDuTemps> findByClasse_NomAndClasse_Filiere_Nom(String nom, String filiereNom);
    List<EmploiDuTemps> findByClasse_NomAndSemestre(String nom, com.class_manager.Gestion_des_emplois.model.entity.Semestre semestre);
    List<EmploiDuTemps> findByClasse_NomAndClasse_Filiere_NomAndSemestre(String nom, String filiereNom, com.class_manager.Gestion_des_emplois.model.entity.Semestre semestre);
}