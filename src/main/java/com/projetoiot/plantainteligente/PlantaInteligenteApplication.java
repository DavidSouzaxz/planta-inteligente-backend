package com.projetoiot.plantainteligente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlantaInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantaInteligenteApplication.class, args);
	}

}
