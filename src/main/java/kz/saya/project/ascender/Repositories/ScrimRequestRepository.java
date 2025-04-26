package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.ScrimRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScrimRequestRepository extends JpaRepository<ScrimRequest, UUID> {
}