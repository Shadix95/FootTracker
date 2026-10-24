package com.foottracker.foottracker.service;

import com.foottracker.foottracker.dto.stats.FavoriteTeamDto;
import com.foottracker.foottracker.dto.stats.MatchSummaryDto;
import com.foottracker.foottracker.entity.FavoriteTeam;
import com.foottracker.foottracker.entity.Match;
import com.foottracker.foottracker.entity.Team;
import com.foottracker.foottracker.repository.FavoriteTeamRepository;
import com.foottracker.foottracker.repository.MatchRepository;
import com.foottracker.foottracker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteTeamService {

    private final FavoriteTeamRepository favoriteTeamRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public List<FavoriteTeamDto> getFavorites() {
        List<FavoriteTeamDto> favorites = new ArrayList<>();
        for (FavoriteTeam favorite : favoriteTeamRepository.findAllByOrderByAddedAtAsc()) {
            favorites.add(toDto(favorite.getTeam()));
        }
        return favorites;
    }

    public FavoriteTeamDto addFavorite(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe introuvable : " + teamId));

        if (favoriteTeamRepository.findByTeamId(teamId).isEmpty()) {
            FavoriteTeam favorite = new FavoriteTeam();
            favorite.setTeam(team);
            favorite.setAddedAt(OffsetDateTime.now());
            favoriteTeamRepository.save(favorite);
        }
        return toDto(team);
    }

    public void removeFavorite(Long teamId) {
        FavoriteTeam favorite = favoriteTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cette equipe n'est pas en favori : " + teamId));
        favoriteTeamRepository.delete(favorite);
    }

    private FavoriteTeamDto toDto(Team team) {
        List<Match> lastMatches = matchRepository.findLastFinishedMatches(team.getId());
        List<Match> upcomingMatches = matchRepository.findUpcomingMatches(team.getId());

        List<String> lastFiveResults = lastMatches.stream()
                .limit(5)
                .map(m -> m.resultFor(team.getId()))
                .toList();

        MatchSummaryDto lastMatch = lastMatches.isEmpty() ? null : MatchSummaryDto.from(lastMatches.get(0));
        MatchSummaryDto nextMatch = upcomingMatches.isEmpty() ? null : MatchSummaryDto.from(upcomingMatches.get(0));

        return new FavoriteTeamDto(team.getId(), team.getName(), team.getCrest(), lastFiveResults, lastMatch, nextMatch);
    }
}
