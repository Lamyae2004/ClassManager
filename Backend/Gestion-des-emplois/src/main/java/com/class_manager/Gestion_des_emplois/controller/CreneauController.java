package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Creneau;
import com.class_manager.Gestion_des_emplois.repository.CreneauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emploi/creneaux")
@RequiredArgsConstructor
public class CreneauController {

    private final CreneauRepository creneauRepository;

    @GetMapping("/{id}")
    public Creneau getCreneauById(@PathVariable Long id) {
        return creneauRepository.findById(id).orElse(null);
    }

    @GetMapping
    public List<Creneau> getAll() {
        return creneauRepository.findAll();
    }
}


