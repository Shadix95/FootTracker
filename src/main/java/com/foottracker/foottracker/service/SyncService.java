package com.foottracker.foottracker.service;

import com.foottracker.foottracker.client.FootballDataClient;
import com.foottracker.foottracker.dto.api.MatchDto;
import com.foottracker.foottracker.dto.api.MatchesResponse;
import com.foottracker.foottracker.dto.api.ScorerDto;
import com.foottracker.foottracker.dto.api.ScorersResponse;
import com.foottracker.foottracker.dto.api.TeamDto;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Scorer;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.ScorerRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final FootballDataClient footballDataClient;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final ScorerRepository scorerRepository;

    @Value("${football-data.competitions}")
    private List<String> competitions;

    @Scheduled(initialDelay = 10_000, fixedDelayString = "${football-data.sync-interval}")
    public void syncAll() {
        for (String code : competitions) {
            try {
                syncCompetition(code);
            } catch (Exception e) {
                log.error("Echec de la synchronisation de {} : {}", code, e.getMessage());
            }
        }
    }

    public void syncCompetition(String code) {
        log.info("Synchronisation de {}...", code);

        MatchesResponse matchesResponse = footballDataClient.getMatches(code);
        for (MatchDto dto : matchesResponse.matches()) {
            saveMatch(dto, code);
        }

        ScorersResponse scorersResponse = footballDataClient.getScorers(code);
        for (ScorerDto dto : scorersResponse.scorers()) {
            saveScorer(dto, code);
        }

        log.info("{} : {} matchs et {} buteurs synchronises", code,
                matchesResponse.matches().size(), scorersResponse.scorers().size());
    }

    private Team saveTeam(TeamDto dto) {
        Team team = teamRepository.findById(dto.id()).orElse(new Team());
        team.setId(dto.id());
        team.setName(dto.name());
        team.setShortName(dto.shortName());
        team.setTla(dto.tla());
        team.setCrest(dto.crest());
        return teamRepository.save(team);
    }

    private void saveMatch(MatchDto dto, String competitionCode) {
        if (dto.homeTeam().id() == null || dto.awayTeam().id() == null) {
            return;
        }

        Match match = matchRepository
                .findById(dto.id())
                .orElse(
                new Match()
        );

        match.setId(dto.id());
        match.setCompetitionCode(competitionCode);
        match.setMatchday(dto.matchday());
        match.setUtcDate(dto.utcDate());
        match.setStatus(dto.status());
        match.setHomeTeam(saveTeam(dto.homeTeam()));
        match.setAwayTeam(saveTeam(dto.awayTeam()));
        match.setHomeScore(dto.score().fullTime().home());
        match.setAwayScore(dto.score().fullTime().away());
        match.setWinner(dto.score().winner());
        matchRepository.save(match);
    }

    private void saveScorer(ScorerDto dto, String competitionCode) {
        Scorer scorer = scorerRepository
                .findByPlayerIdAndCompetitionCode(dto.player().id(), competitionCode)
                .orElse(new Scorer());
        scorer.setPlayerId(dto.player().id());
        scorer.setPlayerName(dto.player().name());
        scorer.setNationality(dto.player().nationality());
        scorer.setCompetitionCode(competitionCode);
        scorer.setTeam(saveTeam(dto.team()));
        scorer.setPlayedMatches(dto.playedMatches());
        scorer.setGoals(dto.goals());
        scorer.setAssists(dto.assists());
        scorer.setPenalties(dto.penalties());
        scorerRepository.save(scorer);
    }
}
