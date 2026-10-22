package com.foottracker.foottracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FootballDataConfig {

    @Bean
    public RestClient footballDataRestClient(
            RestClient.Builder builder,
            @Value("${football-data.base-url}") String baseUrl,
            @Value("${football-data.api-token}") String apiToken) {

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .build();
    }
}
