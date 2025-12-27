package com.class_manager.Gestion_des_emplois.client;

import com.class_manager.Gestion_des_emplois.model.dto.EtudiantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// @FeignClient(name="USER-AUTH-SERVICE" /*, url="http://localhost:9090"*/)
@FeignClient(name = "API-GATEWAY", url = "http://localhost:8080")

public interface EtudiantClient {
    @GetMapping("/api/etudiants/{id}")
    EtudiantDTO getEtudiantById(@PathVariable Long id);

    @GetMapping("/api/etudiants/email")
    EtudiantDTO getEtudiantByEmail(@RequestParam String email);

}
