package com.class_manager.class_responsibility_service.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ResponsibleHistoryDto {

    private Long assignmentId;

    private String firstname;
    private String lastname;
    private String email;

    private Long classId;
    private String niveau;
    private String filiere;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
