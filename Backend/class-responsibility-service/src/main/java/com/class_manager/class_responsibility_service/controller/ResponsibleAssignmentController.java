package com.class_manager.class_responsibility_service.controller;

import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Niveau;
import com.class_manager.class_responsibility_service.model.dto.AssignResponsibleRequest;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleAssignmentDto;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleHistoryDto;
import com.class_manager.class_responsibility_service.model.dto.StudentDto;
import com.class_manager.class_responsibility_service.service.ResponsibleAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Responsables", description = "Gestion des responsables d'étudiants")
@RestController
@RequestMapping("/api/responsible")
@RequiredArgsConstructor

public class ResponsibleAssignmentController {

    private final ResponsibleAssignmentService service ;

    @Operation(summary = "Assigner un responsable aléatoirement à un niveau et une filière",
            description = "Cette opération est réservée aux ADMIN. Elle retourne l'étudiant choisi comme responsable.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-Random")
    public ResponseEntity<StudentDto> assignResponsible(
            @RequestBody AssignResponsibleRequest request) {
        Niveau niveau = request.getNiveau();
        Filiere filiere = request.getFiliere();

        return ResponseEntity.ok(
                service.assignRandomResponsible(niveau,filiere)
        );
    }

    @Operation(summary = "Historique des responsables assignés",
            description = "Retourne la liste de toutes les affectations de responsables effectuées")
    @GetMapping("/history")
    public List<ResponsibleHistoryDto> getHistory() {

        return service.getHistory();

    }

}
