package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final ClasseRepository repo;

    @GetMapping
    public List<Classe> getAll() {
        return repo.findAll();
    }
}