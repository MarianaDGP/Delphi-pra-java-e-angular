package com.empresa.cadfilial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Spring Boot.
 * O STS reconhece esta classe como "Main Type" pelo @SpringBootApplication.
 *
 * Para rodar: botão direito -> Run As -> Spring Boot App
 * A API ficará disponível em http://localhost:8080
 */
@SpringBootApplication
public class CadFilialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadFilialApplication.class, args);
    }
}
