package com.class_manager.class_responsibility_service.client;

import com.class_manager.class_responsibility_service.model.dto.ClassDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EMPLOI-SERVICE",configuration = com.class_manager.class_responsibility_service.config.FeignInternalConfig.class)
public interface ClasseClient {

    @GetMapping("/emploi/classes/search")
    ClassDTO getClassByNiveauAndFiliere(
            @RequestParam("niveau") String niveau,
            @RequestParam("filiere") String filiere
    );

    @GetMapping("/emploi/classes/{id}")
    ClassDTO getClasseById(@PathVariable Long id);
}
