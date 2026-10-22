package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.RecordDto;
import com.foottracker.foottracker.dto.stats.TeamStatsDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamStatsService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public TeamStatsDto getTeamStats(String competitionCode, Long teamId) {
        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Equipe introuvable : " + teamId)
                );

        List<Match> matches = matchRepository.findFinishedMatchesOfTeam(competitionCode, teamId);
        List<Match> homeMatches = matches.stream().filter(m -> m.isHomeTeam(teamId)).toList();
        List<Match> awayMatches = matches.stream().filter(m -> !m.isHomeTeam(teamId)).toList();

        RecordDto total = buildRecord(matches, teamId);

        int cleanSheets = (int) matches.stream()
                .filter(m -> m.goalsAgainst(teamId) == 0)
                .count();

        return new TeamStatsDto(
                team.getId(),
                team.getName(),
                total,
                buildRecord(homeMatches, teamId),
                buildRecord(awayMatches, teamId),
                average(total.goalsFor(), total.played()),
                average(total.goalsAgainst(), total.played()),
                cleanSheets,
                lastFiveResults(matches, teamId)
        );
    }

    private RecordDto buildRecord(List<Match> matches, Long teamId) {
        int wins = 0;
        int draws = 0;
        int losses = 0;
        int goalsFor = 0;
        int goalsAgainst = 0;

        for (Match match : matches) {
            goalsFor += match.goalsFor(teamId);
            goalsAgainst += match.goalsAgainst(teamId);
            switch (match.resultFor(teamId)) {
                case "W" -> wins++;
                case "D" -> draws++;
                default -> losses++;
            }
        }

        int points = wins * 3 + draws;
        return new RecordDto(matches.size(), wins, draws, losses, goalsFor, goalsAgainst, points);
    }

    private List<String> lastFiveResults(List<Match> matches, Long teamId) {
        List<String> results = new ArrayList<>();
        for (int i = matches.size() - 1; i >= 0 && results.size() < 5; i--) {
            results.add(matches.get(i).resultFor(teamId));
        }
        return results;
    }

    private double average(int value, int count) {
        if (count == 0) {
            return 0;
        }
        return Math.round(value * 100.0 / count) / 100.0;
    }
}
