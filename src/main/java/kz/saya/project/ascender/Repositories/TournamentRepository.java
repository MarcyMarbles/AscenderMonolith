package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
}