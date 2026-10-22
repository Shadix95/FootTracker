package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.RankingPointDto;
import com.foottracker.foottracker.dto.stats.StandingRowDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final MatchRepository matchRepository;

    public List<StandingRowDto> getStandings(String competitionCode, Integer upToMatchday) {
        List<Match> matches = findFinishedMatches(competitionCode);
        if (upToMatchday != null) {
            matches = matches.stream().filter(m -> m.getMatchday() <= upToMatchday).toList();
        }
        return computeStandings(matches);
    }

    public List<RankingPointDto> getRankingEvolution(String competitionCode, Long teamId) {
        List<Match> matches = findFinishedMatches(competitionCode);
        int lastMatchday = matches.stream().mapToInt(Match::getMatchday).max().orElse(0);

        List<RankingPointDto> evolution = new ArrayList<>();
        for (int matchday = 1; matchday <= lastMatchday; matchday++) {
            int currentMatchday = matchday;
            List<Match> playedSoFar = matches.stream().filter(m -> m.getMatchday() <= currentMatchday).toList();

            for (StandingRowDto row : computeStandings(playedSoFar)) {
                if (row.teamId().equals(teamId)) {
                    evolution.add(new RankingPointDto(matchday, row.position(), row.points()));
                }
            }
        }
        return evolution;
    }

    private List<Match> findFinishedMatches(String competitionCode) {
        return matchRepository.findByCompetitionCodeAndStatusOrderByUtcDateAsc(competitionCode, "FINISHED");
    }

    private List<StandingRowDto> computeStandings(List<Match> matches) {
        Map<Long, TeamLine> lines = new HashMap<>();

        for (Match match : matches) {
            addResult(lines, match.getHomeTeam(), match);
            addResult(lines, match.getAwayTeam(), match);
        }

        List<TeamLine> sorted = new ArrayList<>(lines.values());
        sorted.sort(Comparator.comparingInt(TeamLine::points)
                .thenComparingInt(TeamLine::goalDifference)
                .thenComparingInt(line -> line.goalsFor)
                .reversed());

        List<StandingRowDto> standings = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            TeamLine line = sorted.get(i);
            standings.add(new StandingRowDto(i + 1, line.team.getId(), line.team.getName(),
                    line.wins + line.draws + line.losses, line.wins, line.draws, line.losses,
                    line.goalsFor, line.goalsAgainst, line.goalDifference(), line.points()));
        }
        return standings;
    }

    private void addResult(Map<Long, TeamLine> lines, Team team, Match match) {
        TeamLine line = lines.computeIfAbsent(team.getId(), id -> new TeamLine(team));
        Long teamId = team.getId();

        line.goalsFor += match.goalsFor(teamId);
        line.goalsAgainst += match.goalsAgainst(teamId);
        switch (match.resultFor(teamId)) {
            case "W" -> line.wins++;
            case "D" -> line.draws++;
            default -> line.losses++;
        }
    }

    private static class TeamLine {
        private final Team team;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;

        TeamLine(Team team) {
            this.team = team;
        }

        int points() {
            return wins * 3 + draws;
        }

        int goalDifference() {
            return goalsFor - goalsAgainst;
        }
    }
}
