package com.class_manager.Gestion_des_emplois.controller;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/filieres")
@RequiredArgsConstructor
public class FiliereController {

    private final FiliereRepository repo;

    @GetMapping
    public List<Filiere> getAll() {
        return repo.findAll();
    }
}