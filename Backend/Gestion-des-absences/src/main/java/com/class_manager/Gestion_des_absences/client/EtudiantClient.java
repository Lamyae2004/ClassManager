package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.EtudiantDTO;
import com.class_manager.Gestion_des_absences.model.dto.StudentDTO;
import com.class_manager.Gestion_des_absences.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="USER-AUTH-SERVICE",configuration = com.class_manager.Gestion_des_absences.config.FeignInternalConfig.class)

public interface EtudiantClient {
    @GetMapping("/api/etudiants/{id}")
    EtudiantDTO getEtudiantById(@PathVariable Long id);

    @GetMapping("/api/users/students")
    List<StudentDTO> getAllStudents();

    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

}
