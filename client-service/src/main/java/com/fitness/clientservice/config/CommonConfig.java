package com.fitness.clientservice.config;

import com.fitness.clientservice.common.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public Status status() {
        return Status.builder()
                .message("Error Occured!")
                .operation("Unknown").build();
    }
}
