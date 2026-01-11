package com.class_manager.Gestion_des_absences.repository;

import com.class_manager.Gestion_des_absences.model.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // Méthodes existantes
    List<Absence> findBySeanceId(Long seanceId);
    List<Absence> findByEtudiantId(Long etudiantId);


    @Query("""
        SELECT a.etudiantId, COUNT(a.id)
        FROM Absence a
        JOIN a.seance s
        WHERE s.profId = :profId
        AND a.present = false
        GROUP BY a.etudiantId
    """)
    List<Object[]> countAbsencesByStudentForProf(@Param("profId") Long profId);


    @Query("""
        SELECT a.etudiantId, COUNT(DISTINCT s.id)
        FROM Absence a
        JOIN a.seance s
        WHERE s.profId = :profId
        GROUP BY a.etudiantId
    """)
    List<Object[]> countTotalSeancesByStudentForProf(@Param("profId") Long profId);


    @Query("""
        SELECT a.etudiantId, COUNT(a.id)
        FROM Absence a
        WHERE a.present = false
        GROUP BY a.etudiantId
    """)
    List<Object[]> countAbsencesByStudent();


    @Query("""
        SELECT a.etudiantId, COUNT(a.id)
        FROM Absence a
        WHERE a.present = false
        AND a.justifie = false
        GROUP BY a.etudiantId
    """)
    List<Object[]> countUnjustifiedAbsencesByStudent();

    /**
     * Trouve toutes les absences non justifiées d'un étudiant
     */
    @Query("""
        SELECT a
        FROM Absence a
        WHERE a.etudiantId = :etudiantId
        AND a.present = false
        AND a.justifie = false
        ORDER BY a.seance.date DESC
    """)
    List<Absence> findUnjustifiedAbsencesByEtudiant(@Param("etudiantId") Long etudiantId);



    @Query("""
        SELECT s.matiereId, COUNT(a.id)
        FROM Absence a
        JOIN a.seance s
        WHERE a.etudiantId = :etudiantId
        AND a.present = false
        GROUP BY s.matiereId
    """)
    List<Object[]> countAbsencesByMatiereForStudent(@Param("etudiantId") Long etudiantId);
}