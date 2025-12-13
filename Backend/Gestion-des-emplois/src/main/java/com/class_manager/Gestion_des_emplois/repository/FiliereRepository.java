package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    Optional<Filiere> findByNom(String nom);
}