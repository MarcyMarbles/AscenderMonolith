package kz.saya.project.ascender.Services;

import kz.saya.project.ascender.Entities.Games;
import kz.saya.project.ascender.Repositories.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GamesService {

    private final GamesRepository gamesRepository;

    @Autowired
    public GamesService(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public List<Games> getAllGames() {
        return gamesRepository.findAll();
    }

    public Optional<Games> getGameById(UUID id) {
        return gamesRepository.findById(id);
    }

    public Games saveGame(Games game) {
        return gamesRepository.save(game);
    }

    public void deleteGame(UUID id) {
        gamesRepository.deleteById(id);
    }

    public List<Games> getScrimableGames() {
        return gamesRepository.findAll().stream()
                .filter(Games::isScrimable)
                .toList();
    }
}
