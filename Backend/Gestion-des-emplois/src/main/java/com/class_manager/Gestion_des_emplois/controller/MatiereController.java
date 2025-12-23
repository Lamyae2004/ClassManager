package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import com.class_manager.Gestion_des_emplois.repository.MatiereRepository;
import com.class_manager.Gestion_des_emplois.service.EmploiImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/matieres")
@RequiredArgsConstructor
public class MatiereController {

    private final MatiereRepository repo;
    private final EmploiImportService emploiImportService;


    @GetMapping("/classe/{id}")
    public List<Matiere> getMatieresByClasse(@PathVariable Long id) {
        return emploiImportService.getMatieresByClasse(id);
    }

    @GetMapping("/classe/prof/{classeId}/{profId}")
    public List<Matiere> getMatieresByClasseAndProf(
            @PathVariable Long classeId,
            @PathVariable Long profId) {
        return emploiImportService.getMatieresByClasseAndProf(classeId, profId);
    }


    @GetMapping
    public List<Matiere> getAll() {
        return repo.findAll();
    }
}
