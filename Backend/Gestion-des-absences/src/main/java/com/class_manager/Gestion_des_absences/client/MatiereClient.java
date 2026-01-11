package com.class_manager.Gestion_des_absences.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "MATIERE-CLIENT", url = "http://localhost:8080")
public interface MatiereClient {
    @GetMapping("/emploi/matieres/{id}")
    Map<String, Object> getMatiereById(@PathVariable Long id);
}


