package kz.saya.project.ascender.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.saya.sbasecore.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Games extends MappedSuperClass {
    private String name; // У игры только 1 название, и нет локализации
    private String description; // Описание игры, которое будет отображаться в приложении
    private String icon; // Иконка игры, которая будет отображаться в приложении
    private String background; // Фоновое изображение игры, которое будет отображаться в приложении
    private String logo; // Логотип игры, который будет отображаться в приложении
    private String website; // Сайт игры, который будет отображаться в приложении
    private boolean scrimable = true; // Возможность создания скримов в игре.
    // Есть игры которые которые не поддерживают кастомные матчи, и такие игры будут использоваться только для поиска тиммейтов
    // Например: Apex Legends, PUBG, Warzone и т.д.
}