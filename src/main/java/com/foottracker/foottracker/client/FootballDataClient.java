package com.foottracker.foottracker.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FootballDataClient {

    private final RestClient footballDataRestClient;

    public String getStandings(String competitionCode) {
        return footballDataRestClient.get()
                .uri("/competitions/{code}/standings", competitionCode)
                .retrieve()
                .body(String.class);
    }

    public String getMatches(String competitionCode) {
        return footballDataRestClient.get()
                .uri("/competitions/{code}/matches", competitionCode)
                .retrieve()
                .body(String.class);
    }

    public String getScorers(String competitionCode) {
        return footballDataRestClient.get()
                .uri("/competitions/{code}/scorers", competitionCode)
                .retrieve()
                .body(String.class);
    }
}
