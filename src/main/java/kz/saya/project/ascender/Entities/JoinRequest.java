package kz.saya.project.ascender.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kz.saya.sbasecore.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "join_request")
public class JoinRequest extends MappedSuperClass {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private OffsetDateTime requestDate = OffsetDateTime.now();

    private OffsetDateTime responseDate;

    private String message;

    private String responseMessage;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
