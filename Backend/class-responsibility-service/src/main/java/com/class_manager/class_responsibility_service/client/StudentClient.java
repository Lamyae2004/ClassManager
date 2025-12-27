package com.class_manager.class_responsibility_service.client;

import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Niveau;
import com.class_manager.class_responsibility_service.model.dto.StudentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-AUTH-SERVICE")
public interface StudentClient {
    @GetMapping("/api/users/students/random")
    StudentDto getRandomStudent(  @RequestParam("niveau") Niveau niveau,
                                  @RequestParam("filiere") Filiere filiere);

    @GetMapping("/api/users/students/{studentId}")
    StudentDto getStudentById( @PathVariable("studentId") Long studentId);
}
