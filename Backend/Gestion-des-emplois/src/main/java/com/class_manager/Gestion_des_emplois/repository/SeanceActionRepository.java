package com.class_manager.Gestion_des_emplois.repository;

import com.class_manager.Gestion_des_emplois.model.entity.SeanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeanceActionRepository extends JpaRepository<SeanceAction, Long> {
    List<SeanceAction> findByEmploiId(Long emploiId);
    List<SeanceAction> findByProfId(Long profId);
}