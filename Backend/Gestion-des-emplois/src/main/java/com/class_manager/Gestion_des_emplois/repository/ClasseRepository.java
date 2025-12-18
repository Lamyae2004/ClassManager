package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    List<Classe> findByNom(String nom); // Changé de Optional à List
    Optional<Classe> findByNomAndFiliere_Nom(String nom, String filiereNom); // Nouvelle méthode

    @Query("SELECT DISTINCT e.classe FROM EmploiDuTemps e WHERE e.profId = :profId")
    List<Classe> findByProfId(@Param("profId") Long profId);

}
