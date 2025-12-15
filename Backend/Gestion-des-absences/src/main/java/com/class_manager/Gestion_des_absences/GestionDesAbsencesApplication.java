package com.class_manager.Gestion_des_absences;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionDesAbsencesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDesAbsencesApplication.class, args);
	}

}
