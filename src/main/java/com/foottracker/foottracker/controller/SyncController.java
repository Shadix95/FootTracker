package com.foottracker.foottracker.controller;

import com.foottracker.foottracker.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping
    public String syncAll() {
        syncService.syncAll();
        return "Synchronisation terminee";
    }

    @PostMapping("/{code}")
    public String syncCompetition(@PathVariable String code) {
        syncService.syncCompetition(code.toUpperCase());
        return "Synchronisation de " + code.toUpperCase() + " terminee";
    }
}
