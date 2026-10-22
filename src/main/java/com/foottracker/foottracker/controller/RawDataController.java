package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.client.FootballDataClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/raw/competitions/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RawDataController {

    private final FootballDataClient footballDataClient;

    @GetMapping("/standings")
    public String standings(@PathVariable String code) {
        return footballDataClient.getStandings(code);
    }

    @GetMapping("/matches")
    public String matches(@PathVariable String code) {
        return footballDataClient.getMatches(code);
    }

    @GetMapping("/scorers")
    public String scorers(@PathVariable String code) {
        return footballDataClient.getScorers(code);
    }
}
