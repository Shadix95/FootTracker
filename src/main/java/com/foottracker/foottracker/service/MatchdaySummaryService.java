package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.MatchSummaryDto;
import com.foottracker.foottracker.dto.stats.MatchdaySummaryDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchdaySummaryService {

    private final MatchRepository matchRepository;

    public MatchdaySummaryDto getSummary(String competitionCode, int matchday) {
        List<Match> allMatches = matchRepository.findByCompetitionCodeAndMatchdayOrderByUtcDateAsc(competitionCode, matchday);
        if (allMatches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journee introuvable : " + matchday);
        }

        List<Match> played = allMatches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .toList();

        int totalGoals = 0;
        int homeWins = 0;
        int awayWins = 0;
        int draws = 0;

        for (Match match : played) {
            totalGoals += totalGoals(match);
            if (match.getHomeScore() > match.getAwayScore()) {
                homeWins++;
            } else if (match.getHomeScore() < match.getAwayScore()) {
                awayWins++;
            } else {
                draws++;
            }
        }

        Match biggestWin = played.stream()
                .filter(m -> goalDifference(m) > 0)
                .max(Comparator.comparingInt(this::goalDifference))
                .orElse(null);

        Match highestScoring = played.stream()
                .max(Comparator.comparingInt(this::totalGoals))
                .orElse(null);

        return new MatchdaySummaryDto(
                matchday,
                played.size(),
                allMatches.size() - played.size(),
                totalGoals,
                played.isEmpty() ? 0 : Math.round(totalGoals * 100.0 / played.size()) / 100.0,
                homeWins,
                awayWins,
                draws,
                biggestWin == null ? null : MatchSummaryDto.from(biggestWin),
                highestScoring == null ? null : MatchSummaryDto.from(highestScoring),
                played.stream().map(MatchSummaryDto::from).toList()
        );
    }

    private int totalGoals(Match match) {
        return match.getHomeScore() + match.getAwayScore();
    }

    private int goalDifference(Match match) {
        return Math.abs(match.getHomeScore() - match.getAwayScore());
    }
}
