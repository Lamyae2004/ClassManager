package com.class_manager.class_responsibility_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import com.class_manager.common.config.FeignInternalConfig;

@SpringBootApplication()
@EnableDiscoveryClient
@EnableFeignClients()
public class ClassResponsibilityServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClassResponsibilityServiceApplication.class, args);
	}
}
