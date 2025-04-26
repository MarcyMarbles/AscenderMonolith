package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, UUID> {
}