package com.class_manager.Gestion_des_emplois.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emploi_du_temps")
public class EmploiDuTemps {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jour;

    @ManyToOne
    private Classe classe;

    @ManyToOne
    private Matiere matiere;

    @ManyToOne
    private Prof prof;

    @ManyToOne
    private Salle salle;

    @ManyToOne
    private Creneau creneau;

    @Enumerated(EnumType.STRING)
    private Semestre semestre;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

}
