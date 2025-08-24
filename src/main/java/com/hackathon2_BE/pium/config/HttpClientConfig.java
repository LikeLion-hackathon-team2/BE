package com.hackathon2_BE.pium.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Value("${external.http.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${external.http.read-timeout-ms:5000}")
    private int readTimeoutMs;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.requestFactory(() -> {
            var f = new SimpleClientHttpRequestFactory();
            f.setConnectTimeout(connectTimeoutMs);
            f.setReadTimeout(readTimeoutMs);
            return f;
        }).build();
    }
}
