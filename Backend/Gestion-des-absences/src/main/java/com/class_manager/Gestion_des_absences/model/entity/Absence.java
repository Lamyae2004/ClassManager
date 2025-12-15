package com.class_manager.Gestion_des_absences.model.entity;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "absence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idEtudiant;   // vient du MS Gestion-des-emplois
    private Long idSeance;     // seance de ce microservice

    private boolean present;
    private boolean justifie;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;
}
