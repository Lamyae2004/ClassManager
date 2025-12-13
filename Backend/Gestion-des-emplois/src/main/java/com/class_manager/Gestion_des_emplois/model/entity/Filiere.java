package com.class_manager.Gestion_des_emplois.model.entity;

import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "filiere")
public class Filiere {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
}
