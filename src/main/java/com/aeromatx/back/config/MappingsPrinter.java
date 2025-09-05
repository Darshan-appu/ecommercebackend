package com.aeromatx.back.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class MappingsPrinter {

    @Bean
    public CommandLineRunner printMappings(RequestMappingHandlerMapping mapping) {
        return args -> {
            System.out.println("=== Registered request mappings ===");
            for (RequestMappingInfo info : mapping.getHandlerMethods().keySet()) {
                System.out.println(info);
            }
            System.out.println("=== end mappings ===");
        };
    }
}
