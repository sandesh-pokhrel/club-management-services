package com.fitness.purchaseservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * This is swagger configuration class
 * To access the swagger API docs - http://localhost:8081/v2/api-docs
 * To access the swagger ui use -  http://localhost:8081/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fitness.clientservice"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Club management client application",
                "Club management spring boot backend rest application.",
                "1.0",
                "https://sandesh-pokhrel.com",
                new Contact("Sandesh Pokhrel", "https://sandesh.com", "sandesh.pokhrel56@gmail.com"),
                "Club-Management Copyright@2021 License",
                "https://sandesh-pokhrel-license.com",
                Collections.emptyList()
        );
    }
}
