package com.class_manager.Gestion_des_absences.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seance")
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long profId;
    private Long classeId;
    private Long creneauId;
    private LocalDate date;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL)
    private List<Absence> absences;



}