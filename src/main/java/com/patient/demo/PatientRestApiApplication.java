package com.patient.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PatientRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatientRestApiApplication.class, args);
	}

}
