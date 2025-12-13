package com.class_manager.Gestion_des_emplois;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GestionDesEmploisApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDesEmploisApplication.class, args);
	}


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")  // tous les endpoints
						.allowedOrigins("http://localhost:5173")  // ton React dev server
						.allowedMethods("*")  // GET, POST, PUT, DELETE...
						.allowedHeaders("*");
			}
		};
	}

}
