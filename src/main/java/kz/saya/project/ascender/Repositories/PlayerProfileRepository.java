package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.PlayerProfile;
import kz.saya.sbasecore.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, UUID> {
    Optional<PlayerProfile> findByUser(User user);
}
