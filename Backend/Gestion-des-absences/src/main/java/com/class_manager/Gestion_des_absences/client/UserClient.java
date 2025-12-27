package com.class_manager.Gestion_des_absences.client;


import com.class_manager.Gestion_des_absences.model.dto.StudentDTO;
import com.class_manager.Gestion_des_absences.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


//@FeignClient(name = "USER-AUTH-SERVICE")

@FeignClient(name = "API-GATEWAY", url = "http://localhost:8080")
public interface UserClient {

    // Récupérer tous les utilisateurs
    @GetMapping("/api/users")
    List<UserDTO> getAllUsers();

    // Récupérer un utilisateur par ID
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    // Récupérer un utilisateur par prénom + nom
    @GetMapping("/api/users/search")
    UserDTO getUserByFullName(
            @RequestParam String firstname,
            @RequestParam String lastname
    );


    @GetMapping("/api/users/students")
    List<StudentDTO> getAllStudents();



}
