package com.class_manager.class_responsibility_service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Data
@Table(name = "responsible_assignment")
@Builder
@AllArgsConstructor
public class ResponsibleAssignment {
    @Id
    @GeneratedValue
    private Long id ;
    private Long studentId;
    private Long classId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
