package com.fitness.clientservice.config;

import com.fitness.sharedapp.common.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FieldConfigProperties {

    @Bean
    @ConfigurationProperties("spring.mail")
    public MailProperties mailProperties() {
        return new MailProperties();
    }
}
