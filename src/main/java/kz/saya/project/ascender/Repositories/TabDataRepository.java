package kz.saya.project.ascender.Repositories;

import kz.saya.project.ascender.Entities.TabData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TabDataRepository extends JpaRepository<TabData, UUID> {
}