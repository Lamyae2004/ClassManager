package com.class_manager.user_auth_service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "students")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Student extends User{
    private String apogeeNumber;
    @Enumerated(EnumType.STRING)
    private Filiere filiere;
    @Enumerated(EnumType.STRING)
    private Niveau niveau;
}
