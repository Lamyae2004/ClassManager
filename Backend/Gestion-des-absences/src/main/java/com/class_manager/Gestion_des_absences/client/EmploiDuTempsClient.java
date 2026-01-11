package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.ClassDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "EMPLOI-SERVICE",
        configuration = com.class_manager.Gestion_des_absences.config.FeignInternalConfig.class
)
public interface EmploiDuTempsClient {



    @GetMapping("/classes/prof/{id}")
    List<ClassDTO> getClassesByProf(@PathVariable Long id);
}
