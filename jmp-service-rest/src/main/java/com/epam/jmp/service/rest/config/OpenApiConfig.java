package com.epam.jmp.service.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jmpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JMP REST API")
                        .description("Java Mentoring Program - REST API for User and Subscription management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EPAM JMP")
                                .email("jmp@epam.com")));
    }
}
