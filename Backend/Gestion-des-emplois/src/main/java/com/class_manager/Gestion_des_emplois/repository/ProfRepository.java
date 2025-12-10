package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Prof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfRepository extends JpaRepository<Prof, Long> {

    // Méthode nécessaire pour FichierEmploiService.getOrCreateProf
    Optional<Prof> findByNom(String nom);
}