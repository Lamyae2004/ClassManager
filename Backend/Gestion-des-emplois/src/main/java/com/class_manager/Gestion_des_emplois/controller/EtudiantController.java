package com.class_manager.Gestion_des_emplois.controller;


import com.class_manager.Gestion_des_emplois.model.entity.Etudiant;
import com.class_manager.Gestion_des_emplois.repository.EtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantRepository repo;

    @GetMapping("/{id}")
    public Etudiant getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @GetMapping("/classe/{idClasse}")
    public List<Etudiant> getByClasse(@PathVariable Long idClasse) {
        return repo.findByClasseId(idClasse);
    }
}
