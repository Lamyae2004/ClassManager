package com.class_manager.class_responsibility_service.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ResponsibleAssignmentDto {
    private Long id ;
    private Long studentId;
    private Long classId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
