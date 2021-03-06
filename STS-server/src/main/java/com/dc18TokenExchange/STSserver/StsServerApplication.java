package com.dc18TokenExchange.STSserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StsServerApplication.class, args);
	}
}
