package com.moura.agrios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // <--- atualiza o vencimento de um recebimento
@SpringBootApplication
public class AgriosApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgriosApplication.class, args);
	}

}
