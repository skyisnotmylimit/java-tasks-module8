package com.epam.jmp.service.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.epam.jmp.service.rest",
        "com.epam.jmp.cloud.service.impl",
        "com.epam.jmp.service.api"
})
@EntityScan("com.epam.jmp.domain")
@EnableJpaRepositories("com.epam.jmp.service.api")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
