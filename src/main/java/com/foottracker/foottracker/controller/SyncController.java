package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Synchronisation", description = "Import des donnees de football-data.org vers la base")
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @Operation(summary = "Synchroniser toutes les competitions configurees")
    @PostMapping
    public String syncAll() {
        syncService.syncAll();
        return "Synchronisation terminee";
    }

    @Operation(summary = "Synchroniser une competition", description = "Environ 12 secondes a cause de la limite de 10 requetes par minute")
    @PostMapping("/{code}")
    public String syncCompetition(@Parameter(example = "FL1") @PathVariable String code) {
        syncService.syncCompetition(code.toUpperCase());
        return "Synchronisation de " + code.toUpperCase() + " terminée";
    }
}
