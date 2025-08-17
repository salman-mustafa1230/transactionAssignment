package com.example.transaction_assignment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Transaction API")
                        .version("1.0.0")
                        .description("API for managing transactions")
                        .contact(new Contact()
                                .name("Salman Mustafa")
                                .email("your-email@example.com")
                        )
                )
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"));
    }
//    @Bean
//    public GroupedOpenApi transactionApi() {
//        return GroupedOpenApi.builder()
//                .group("transactions")
//                .pathsToMatch("/api/**")
//                .packagesToScan("com.example.transaction_assignment.web")
//                .build();
//    }
}

