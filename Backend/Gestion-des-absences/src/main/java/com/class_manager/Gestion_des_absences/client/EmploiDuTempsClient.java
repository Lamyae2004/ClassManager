package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.ClassDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "EMPLOI-SERVICE",
        configuration = com.class_manager.Gestion_des_absences.config.FeignInternalConfig.class
)
public interface EmploiDuTempsClient {

    @GetMapping("/classes/prof/{id}")
    List<ClassDTO> getClassesByProf(@PathVariable Long id);

    @GetMapping("/emploi/matieres/{id}")
    Map<String, Object> getMatiereById(@PathVariable Long id);

    @GetMapping("/emploi/creneaux/{id}")
    Map<String, Object> getCreneauById(@PathVariable Long id);
}
