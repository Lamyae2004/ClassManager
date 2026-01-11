package com.class_manager.Gestion_des_absences.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInternalConfig {
    @Bean
    public RequestInterceptor internalRequestInterceptor() {
        return requestTemplate -> requestTemplate.header("X-Internal-Call", "true");
    }
}