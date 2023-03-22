package com.example.testservice.config.resttemplate;

import org.apache.http.HttpHeaders;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
