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


    @GetMapping
    public Filiere[] getAll() {
        return Filiere.values();
    }
}