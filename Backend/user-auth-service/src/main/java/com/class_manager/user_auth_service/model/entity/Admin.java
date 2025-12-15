package com.class_manager.user_auth_service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "admins")
@SuperBuilder
@NoArgsConstructor
public class Admin extends User{

}
