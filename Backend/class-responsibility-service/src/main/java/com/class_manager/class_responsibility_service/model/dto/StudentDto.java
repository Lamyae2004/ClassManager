package com.class_manager.class_responsibility_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto extends UserDto{
        private String apogeeNumber;
        private String filiere;
        private String niveau;
}
