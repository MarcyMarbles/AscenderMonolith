package kz.saya.project.ascender.Services;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("teamSecurity")
public class TeamSecurity {
    private final TeamService teamService;
    private final JoinRequestService joinRequestService;

    public TeamSecurity(TeamService teamService, JoinRequestService joinRequestService) {
        this.teamService = teamService;
        this.joinRequestService = joinRequestService;
    }

    public boolean isTeamCreator(String login, UUID joinRequestId) {
        var jr = joinRequestService.getJoinRequestById(joinRequestId).orElse(null);
        return jr != null && jr.getTeam().getCreator().getUser().getLogin().equals(login);
    }
}
