package com.class_manager.Gestion_des_absences.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "CRENEAU-CLIENT", url = "http://localhost:8080")
public interface CreneauClient {
    @GetMapping("/emploi/creneaux/{id}")
    Map<String, Object> getCreneauById(@PathVariable Long id);
}


