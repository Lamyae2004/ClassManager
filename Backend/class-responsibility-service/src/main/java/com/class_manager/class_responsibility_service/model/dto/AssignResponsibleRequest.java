package com.class_manager.class_responsibility_service.model.dto;

import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Niveau;
import lombok.Data;

@Data
public class AssignResponsibleRequest {
    private Niveau niveau;
    private Filiere filiere;
}
