package kz.saya.project.ascender.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.saya.sbase.Entity.FileDescriptor;
import kz.saya.sbase.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "player_profile")
public class PlayerProfile extends MappedSuperClass {
    private String callingName; // Имя по которому стоит звать игрока
    private String steamId; // Идентификатор игрока в Steam -> Буду брать из Steam API
    private FileDescriptor avatar; // Аватарка игрока, которая будет отображаться в приложении

    public byte[] getAvatarData() {
        return avatar.getFileData();
    }
}