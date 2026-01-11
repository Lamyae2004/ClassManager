package com.class_manager.user_auth_service.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacheremploiDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String speciality;
}
