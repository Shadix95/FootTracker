package com.foottracker.foottracker.repository;

import com.foottracker.foottracker.entity.FavoriteTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteTeamRepository extends JpaRepository<FavoriteTeam, Long> {

    Optional<FavoriteTeam> findByTeamId(Long teamId);

    List<FavoriteTeam> findAllByOrderByAddedAtAsc();
}
