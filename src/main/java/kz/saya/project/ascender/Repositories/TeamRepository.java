package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
}