package com.iharshashkou.internetstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InternetstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternetstoreApplication.class, args);
	}

}
