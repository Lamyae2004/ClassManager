package com.class_manager.Gestion_des_absences.repository;


import com.class_manager.Gestion_des_absences.model.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    //List<Absence> findByIdSeance(Long idSeance);
    //List<Absence> findByIdEtudiant(Long idEtudiant);
    @Query("""
       SELECT a.etudiantId, COUNT(a)
       FROM Absence a
       WHERE a.present = false
       GROUP BY a.etudiantId
    """)
    List<Object[]> countAbsencesByStudent();

    @Query("""
    SELECT a.etudiantId, COUNT(a)
    FROM Absence a
    JOIN a.seance s
    WHERE s.profId = :profId
      AND a.present = false
    GROUP BY a.etudiantId
""")
    List<Object[]> countAbsencesByStudentForProf(@Param("profId") Long profId);


    @Query("""
    SELECT a.etudiantId, COUNT(a)
    FROM Absence a
    JOIN a.seance s
    WHERE s.profId = :profId
    GROUP BY a.etudiantId
""")
    List<Object[]> countTotalSeancesByStudentForProf(@Param("profId") Long profId);

}
