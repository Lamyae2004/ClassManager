package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {

    // Méthode nécessaire pour FichierEmploiService.getOrCreateCreneau
    Optional<Creneau> findByHeureDebutAndHeureFin(String heureDebut, String heureFin);
}