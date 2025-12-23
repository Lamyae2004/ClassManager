package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.EtudiantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="USER-AUTH-SERVICE", url="http://localhost:9090")

public interface EtudiantClient {
    @GetMapping("/api/etudiants/{id}")
    EtudiantDTO getEtudiantById(@PathVariable Long id);
}
