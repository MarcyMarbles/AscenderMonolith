package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.Scrim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface ScrimRepository extends JpaRepository<Scrim, UUID> {
}