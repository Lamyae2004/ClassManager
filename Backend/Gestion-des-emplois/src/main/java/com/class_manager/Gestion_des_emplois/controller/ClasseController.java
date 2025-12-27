package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.model.dto.ClasseDTO;
import com.class_manager.Gestion_des_emplois.model.dto.EmploiDuTempsDTO;
import com.class_manager.Gestion_des_emplois.model.entity.Classe;
import com.class_manager.Gestion_des_emplois.repository.ClasseRepository;
import com.class_manager.Gestion_des_emplois.service.ClasseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emploi/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final ClasseRepository repo;
    private final ClasseService classeService;

    @GetMapping
    public List<Classe> getAll() {
        return repo.findAll();
    }

    @GetMapping("/prof/{id}")
    public List<ClasseDTO> getClassesByProfId(@PathVariable Long id) {
        return classeService.getClassesByTeacherId(id);
    }




}