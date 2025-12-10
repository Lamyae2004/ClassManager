package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

    // Méthode nécessaire pour FichierEmploiService.getOrCreateSalle
    Optional<Salle> findByNom(String nom);
}