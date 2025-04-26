package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.sbase.Entity.FileDescriptor;
import kz.saya.sbase.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "player_profile")
public class PlayerProfile extends MappedSuperClass {
    private String callingName; // Имя по которому стоит звать игрока
    private String fullName; // Полное имя игрока
    private String email; // Email адрес игрока
    private String steamId; // Идентификатор игрока в Steam -> Буду брать из Steam API
    private String discordId; // Discord ID игрока
    private String twitchUsername; // Twitch username игрока
    private String youtubeChannel; // YouTube канал игрока
    private String bio; // Биография или описание игрока
    private LocalDate birthDate; // Дата рождения игрока
    private String country; // Страна проживания
    private String city; // Город проживания
    private String language; // Основной язык общения

    @OneToOne
    @JoinColumn(name = "avatar_id")
    private FileDescriptor avatar; // Аватарка игрока, которая будет отображаться в приложении

    @OneToOne
    @JoinColumn(name = "profile_background_id")
    private FileDescriptor profileBackground; // Фоновое изображение профиля

    private String skillLevel; // Уровень навыка игрока (например, Beginner, Intermediate, Advanced, Pro)
    private Integer totalMatchesPlayed; // Общее количество сыгранных матчей
    private Integer totalWins; // Общее количество побед
    private Double winRate; // Процент побед

    @ManyToMany
    @JoinTable(
        name = "player_preferred_games",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Games> preferredGames; // Предпочитаемые игры

    @ElementCollection
    @CollectionTable(name = "player_achievements", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "achievement")
    private Set<String> achievements; // Достижения игрока

    private boolean lookingForTeam; // Флаг, указывающий, ищет ли игрок команду
    private String availability; // Доступность игрока (например, "Weekends", "Evenings", "Anytime")
    private String timezone; // Часовой пояс игрока

    public byte[] getAvatarData() {
        return avatar != null ? avatar.getFileData() : null;
    }

    public byte[] getProfileBackgroundData() {
        return profileBackground != null ? profileBackground.getFileData() : null;
    }
}
