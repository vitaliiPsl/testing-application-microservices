package com.example.attemptservice.config.resttemplate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplateAuthInterceptor interceptor = new RestTemplateAuthInterceptor();
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();

        return restTemplateBuilder
                .errorHandler(errorHandler)
                .additionalInterceptors(interceptor)
                .build();
    }
}
