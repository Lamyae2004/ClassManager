package com.class_manager.Gestion_des_emplois.client;


import com.class_manager.Gestion_des_emplois.model.dto.EtudiantDTO;
import com.class_manager.Gestion_des_emplois.model.dto.TeacherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "USER-AUTH-SERVICE"
)
public interface TeacherClient {
    @GetMapping("/api/users/teachers")
    List<TeacherDTO> getAllTeachers();

    @GetMapping("/api/users/teachers/{id}")
    TeacherDTO getTeacherById(@PathVariable("id") Long id);

  /*  @GetMapping("/api/users/teachers")
    TeacherDTO getTeacherById(@PathVariable Long id);*/


    @GetMapping("/api/users/students/{studentId}")
    EtudiantDTO getEtudiantById(@PathVariable Long studentId);


    @GetMapping("/api/users/teachers/search")
    TeacherDTO getTeacherByFullName(
            @RequestParam String firstname,
            @RequestParam String lastname
    );

    @GetMapping("/api/users/students/count-by-class")
    Map<String, Integer> countStudentsByClass(
            @RequestParam String niveau,
            @RequestParam String filiere,
            @RequestParam boolean activated
    );


}
