package com.class_manager.Gestion_des_emplois.client;


import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "USER-AUTH-SERVICE"
)
public interface TeacherClient {
    @GetMapping("/teachers")
    List<TeacherDTO> getAllTeachers();

    @GetMapping("/api/users/teachers")
    TeacherDTO getTeacherById(@PathVariable Long id);


    @GetMapping("/api/users/teachers/search")
    TeacherDTO getTeacherByFullName(
            @RequestParam String firstname,
            @RequestParam String lastname
    );



}
