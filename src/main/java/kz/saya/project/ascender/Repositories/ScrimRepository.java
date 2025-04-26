package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Scrim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScrimRepository extends JpaRepository<Scrim, UUID> {
}