package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Matiere;
import com.class_manager.Gestion_des_emplois.repository.MatiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/matieres")
@RequiredArgsConstructor
public class MatiereController {

    private final MatiereRepository repo;

    @GetMapping
    public List<Matiere> getAll() {
        return repo.findAll();
    }
}
