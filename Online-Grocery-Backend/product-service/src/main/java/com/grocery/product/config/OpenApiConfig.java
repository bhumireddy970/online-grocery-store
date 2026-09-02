package com.grocery.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI groceryOpenApi() {

        Contact contact = new Contact();

        contact.setName("Online Grocery Store");

        contact.setEmail("admin@grocery.com");

        Info info = new Info();

        info.setTitle("Product Service API");

        info.setVersion("1.0");

        info.setDescription("Product Catalog Microservice");

        info.setContact(contact);

        return new OpenAPI()

                .info(info);

    }

}