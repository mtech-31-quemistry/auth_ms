package com.quemistry.auth_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        // Do any additional configuration here
        return new RestTemplate();
    }
}
