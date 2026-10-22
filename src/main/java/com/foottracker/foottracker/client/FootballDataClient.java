package com.foottracker.foottracker.client;

import com.foottracker.foottracker.dto.api.MatchesResponse;
import com.foottracker.foottracker.dto.api.ScorersResponse;
import com.foottracker.foottracker.dto.api.StandingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FootballDataClient {

    private static final long MIN_DELAY_BETWEEN_CALLS_MS = 6000;

    private final RestClient footballDataRestClient;

    private long lastCallTime = 0;

    @Cacheable("standings")
    public StandingsResponse getStandings(String competitionCode) {
        waitForRateLimit();
        return footballDataRestClient.get()
                .uri("/competitions/{code}/standings", competitionCode)
                .retrieve()
                .body(StandingsResponse.class);
    }

    @Cacheable("matches")
    public MatchesResponse getMatches(String competitionCode) {
        waitForRateLimit();
        return footballDataRestClient.get()
                .uri("/competitions/{code}/matches", competitionCode)
                .retrieve()
                .body(MatchesResponse.class);
    }

    @Cacheable("scorers")
    public ScorersResponse getScorers(String competitionCode) {
        waitForRateLimit();
        return footballDataRestClient.get()
                .uri("/competitions/{code}/scorers?limit=50", competitionCode)
                .retrieve()
                .body(ScorersResponse.class);
    }

    private synchronized void waitForRateLimit() {
        long waitTime = lastCallTime + MIN_DELAY_BETWEEN_CALLS_MS - System.currentTimeMillis();
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastCallTime = System.currentTimeMillis();
    }
}
