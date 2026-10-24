package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.HeadToHeadDto;
import com.foottracker.foottracker.dto.stats.MatchSummaryDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadToHeadService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public HeadToHeadDto getHeadToHead(String competitionCode, Long team1Id, Long team2Id) {
        if (team1Id.equals(team2Id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les deux equipes doivent etre differentes");
        }

        Team team1 = findTeam(team1Id);
        Team team2 = findTeam(team2Id);
        List<Match> matches = matchRepository.findHeadToHead(competitionCode, team1Id, team2Id);

        int team1Wins = 0;
        int team2Wins = 0;
        int draws = 0;
        int team1Goals = 0;
        int team2Goals = 0;

        for (Match match : matches) {
            team1Goals += match.goalsFor(team1Id);
            team2Goals += match.goalsFor(team2Id);
            switch (match.resultFor(team1Id)) {
                case "W" -> team1Wins++;
                case "L" -> team2Wins++;
                default -> draws++;
            }
        }

        List<MatchSummaryDto> summaries = matches.stream().map(MatchSummaryDto::from).toList();

        return new HeadToHeadDto(team1.getName(), team2.getName(), matches.size(),
                team1Wins, team2Wins, draws, team1Goals, team2Goals, summaries);
    }

    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe introuvable : " + teamId));
    }
}
