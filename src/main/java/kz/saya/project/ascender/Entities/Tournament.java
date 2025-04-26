package kz.saya.project.ascender.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.saya.sbase.Entity.MappedLocalizedClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tournament")
public class Tournament extends MappedLocalizedClass {
}