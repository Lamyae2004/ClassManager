package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {
  /*  List<EmploiDuTemps> findByClasse_Nom(String nom);

    // Ici, filiere est maintenant un enum, donc tu précises son type
    List<EmploiDuTemps> findByClasse_NomAndClasse_Filiere(String nom, Filiere filiere);

    List<EmploiDuTemps> findByClasse_NomAndSemestre(String nom, com.class_manager.Gestion_des_emplois.model.entity.Semestre semestre);

    // Plus besoin de "_Nom" pour filiere, juste le champ enum
    List<EmploiDuTemps> findByClasse_NomAndClasse_FiliereAndSemestre(String nom, Filiere filiere, com.class_manager.Gestion_des_emplois.model.entity.Semestre semestre);
*/

    List<EmploiDuTemps> findByClasseId(Long classeId);

    @Query("SELECT DISTINCT e.matiere FROM EmploiDuTemps e WHERE e.classe.id = :classeId AND e.profId = :profId")
    List<Matiere> findMatieresByClasseAndProf(@Param("classeId") Long classeId, @Param("profId") Long profId);

    @Query("SELECT DISTINCT e.classe FROM EmploiDuTemps e WHERE e.profId = :profId")
    List<Classe> findDistinctClassesByProfId(Long profId);

    @Query("""
        SELECT e FROM EmploiDuTemps e
        WHERE e.classe.nom = :nom
        AND e.classe.filiere = :filiere
    """)
    List<EmploiDuTemps> findByClasseNomAndFiliere(
            @Param("nom") String nom,
            @Param("filiere") Filiere filiere
    );
}

