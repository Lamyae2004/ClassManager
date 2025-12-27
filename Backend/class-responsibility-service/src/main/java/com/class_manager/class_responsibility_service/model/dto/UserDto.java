package com.class_manager.class_responsibility_service.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Data
@AllArgsConstructor
public class UserDto {
    private Long id ;
    private String firstname;
    private String lastname;
    private String email;
   // private String password;
    private String role;
    private boolean isActivated = false;
}
