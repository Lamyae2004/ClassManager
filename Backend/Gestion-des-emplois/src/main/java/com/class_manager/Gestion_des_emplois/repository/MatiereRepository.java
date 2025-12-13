package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {

    // Méthode nécessaire pour FichierEmploiService.getOrCreateMatiere
    Optional<Matiere> findByNom(String nom);
}