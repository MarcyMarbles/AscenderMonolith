package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, UUID> {
}