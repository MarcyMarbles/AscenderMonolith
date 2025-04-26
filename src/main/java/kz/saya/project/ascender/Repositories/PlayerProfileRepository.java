package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, UUID> {
}