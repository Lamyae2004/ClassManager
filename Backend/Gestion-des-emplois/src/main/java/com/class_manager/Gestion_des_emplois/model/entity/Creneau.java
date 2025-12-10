package com.class_manager.Gestion_des_emplois.model.entity;


import com.class_manager.Gestion_des_emplois.model.entity.EmploiDuTemps;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "creneau")
public class Creneau {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String heureDebut;
    private String heureFin;
}