package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.ClassDTO;
import com.class_manager.Gestion_des_absences.model.dto.EmploiDuTempsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "EMPLOI-SERVICE"
)
public interface EmploiDuTempsClient {

    @GetMapping("/emplois/{id}")
    EmploiDuTempsDTO getEmploiById(@PathVariable Long id);

    @GetMapping("/classes/prof/{id}")
    List<ClassDTO> getClassesByProf(@PathVariable Long id);
}
