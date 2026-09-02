package com.grocery.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderApi() {

        Contact contact = new Contact();

        contact.setName("Online Grocery Store");

        contact.setEmail("admin@grocery.com");

        Info info = new Info();

        info.setTitle("order Service API");

        info.setVersion("1.0");

        info.setDescription("order Management Microservice");

        info.setContact(contact);

        return new OpenAPI().info(info);

    }

}