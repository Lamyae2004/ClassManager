package com.class_manager.Gestion_des_emplois.controller;
import com.class_manager.Gestion_des_emplois.model.entity.Salle;
import com.class_manager.Gestion_des_emplois.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleRepository repo;

    @GetMapping
    public List<Salle> getAll() {
        return repo.findAll();
    }
}
