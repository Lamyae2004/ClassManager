package com.class_manager.user_auth_service.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true) // ou false si tu ne veux pas inclure parent
@NoArgsConstructor
@SuperBuilder
public class AdminDto extends UserDto{
}
