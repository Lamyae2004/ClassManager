package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.EmploiDuTempsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "gestion-emplois",
        url = "http://localhost:8081"   // port du MS Gestion-des-emplois
)
public interface EmploiDuTempsClient {

    @GetMapping("/emplois/{id}")
    EmploiDuTempsDTO getEmploiById(@PathVariable Long id);
}
