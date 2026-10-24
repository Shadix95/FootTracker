package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.dto.stats.FavoriteTeamDto;
import com.foottracker.foottracker.service.FavoriteTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Equipes favorites", description = "Suivi des equipes favorites : forme, dernier et prochain match")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteTeamController {

    private final FavoriteTeamService favoriteTeamService;

    @Operation(summary = "Lister les equipes favorites avec leur bilan")
    @GetMapping
    public List<FavoriteTeamDto> getFavorites() {
        return favoriteTeamService.getFavorites();
    }

    @Operation(summary = "Ajouter une equipe aux favoris")
    @PostMapping("/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteTeamDto addFavorite(@Parameter(example = "524") @PathVariable Long teamId) {
        return favoriteTeamService.addFavorite(teamId);
    }

    @Operation(summary = "Retirer une equipe des favoris")
    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@Parameter(example = "524") @PathVariable Long teamId) {
        favoriteTeamService.removeFavorite(teamId);
    }
}
