package com.class_manager.user_auth_service.model.dto;

import lombok.Builder;

@Builder
public record MailBody(String to ,String subject,String text) {
}
