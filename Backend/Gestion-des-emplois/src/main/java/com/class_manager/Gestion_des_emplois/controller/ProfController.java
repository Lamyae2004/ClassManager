package com.class_manager.Gestion_des_emplois.controller;
import com.class_manager.Gestion_des_emplois.model.entity.Filiere;
import com.class_manager.Gestion_des_emplois.model.entity.Prof;
import com.class_manager.Gestion_des_emplois.repository.ProfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profs")
@RequiredArgsConstructor
public class ProfController {

    private final ProfRepository repo;

    @GetMapping
    public List<Prof> getAll() {
        return repo.findAll();
    }
}
