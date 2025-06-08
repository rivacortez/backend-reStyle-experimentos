package com.metasoft.restyle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RestyleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestyleApplication.class, args);
	}

}
