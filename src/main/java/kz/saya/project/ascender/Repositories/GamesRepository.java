package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Games;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GamesRepository extends JpaRepository<Games, UUID> {
}