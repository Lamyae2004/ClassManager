package com.class_manager.Gestion_des_absences.repository;


import com.class_manager.Gestion_des_absences.model.entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {


}