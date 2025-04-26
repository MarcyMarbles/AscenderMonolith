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

    /**
     * Get all games
     * @return List of all games
     */
    public List<Games> getAllGames() {
        return gamesRepository.findAll();
    }

    /**
     * Get game by ID
     * @param id Game ID
     * @return Optional containing the game if found
     */
    public Optional<Games> getGameById(UUID id) {
        return gamesRepository.findById(id);
    }

    /**
     * Save a game
     * @param game Game to save
     * @return Saved game
     */
    public Games saveGame(Games game) {
        return gamesRepository.save(game);
    }

    /**
     * Delete a game
     * @param id Game ID
     */
    public void deleteGame(UUID id) {
        gamesRepository.deleteById(id);
    }

    /**
     * Get all games that support scrims
     * @return List of games that support scrims
     */
    public List<Games> getScrimableGames() {
        return gamesRepository.findAll().stream()
                .filter(Games::isScrimable)
                .toList();
    }
}
