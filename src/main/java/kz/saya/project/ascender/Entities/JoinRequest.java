package kz.saya.project.ascender.Entities;

import jakarta.persistence.*;
import kz.saya.sbase.Entity.MappedSuperClass;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "join_request")
public class JoinRequest extends MappedSuperClass {
    
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    
    private OffsetDateTime requestDate = OffsetDateTime.now();
    
    private OffsetDateTime responseDate;
    
    private String message; // Optional message from team creator
    
    private String responseMessage; // Optional message from tournament organizer
    
    // Enum for request status
    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}