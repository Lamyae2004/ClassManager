package com.class_manager.Gestion_des_emplois;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableFeignClients()
@SpringBootApplication()
@EnableDiscoveryClient
public class GestionDesEmploisApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDesEmploisApplication.class, args);
	}

}
