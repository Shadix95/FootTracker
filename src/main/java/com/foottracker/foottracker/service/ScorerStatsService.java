package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.ScorerStatsDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Scorer;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.ScorerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScorerStatsService {

    private final ScorerRepository scorerRepository;
    private final MatchRepository matchRepository;

    public List<ScorerStatsDto> getScorerStats(String competitionCode) {
        Map<Long, Integer> goalsByTeam = countGoalsByTeam(competitionCode);

        List<ScorerStatsDto> stats = new ArrayList<>();
        for (Scorer scorer : scorerRepository.findByCompetitionCodeOrderByGoalsDesc(competitionCode)) {
            int goals = valueOrZero(scorer.getGoals());
            int penalties = valueOrZero(scorer.getPenalties());
            int playedMatches = valueOrZero(scorer.getPlayedMatches());
            int teamGoals = goalsByTeam.getOrDefault(scorer.getTeam().getId(), 0);

            stats.add(new ScorerStatsDto(
                    scorer.getPlayerName(),
                    scorer.getTeam().getName(),
                    playedMatches,
                    goals,
                    penalties,
                    goals - penalties,
                    ratio(goals, playedMatches),
                    teamGoals,
                    ratio(goals * 100, teamGoals)
            ));
        }
        return stats;
    }

    private Map<Long, Integer> countGoalsByTeam(String competitionCode) {
        Map<Long, Integer> goalsByTeam = new HashMap<>();
        for (Match match : matchRepository.findByCompetitionCodeAndStatusOrderByUtcDateAsc(competitionCode, "FINISHED")) {
            goalsByTeam.merge(match.getHomeTeam().getId(), match.getHomeScore(), Integer::sum);
            goalsByTeam.merge(match.getAwayTeam().getId(), match.getAwayScore(), Integer::sum);
        }
        return goalsByTeam;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double ratio(int value, int total) {
        if (total == 0) {
            return 0;
        }
        return Math.round(value * 100.0 / total) / 100.0;
    }
}
