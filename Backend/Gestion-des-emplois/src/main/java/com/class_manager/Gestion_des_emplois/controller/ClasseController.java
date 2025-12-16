package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.dto.ClasseDTO;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import com.class_manager.Gestion_des_emplois.service.ClasseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final ClasseRepository repo;
    private final ClasseService classeService;

    @GetMapping
    public List<Classe> getAll() {
        return repo.findAll();
    }

    @GetMapping("/prof/{profId}")
    public List<ClasseDTO> getClassesByProf(@PathVariable Long profId) {
        return classeService.getClassesByProf(profId);
    }


}