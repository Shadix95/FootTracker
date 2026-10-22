package com.foottracker.foottracker.repository;

import com.foottracker.foottracker.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
