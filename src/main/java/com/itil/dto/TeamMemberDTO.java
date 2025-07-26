package com.itil.dto;

import com.itil.entity.Gatekeeper;
import com.itil.entity.Ticket;
import com.itil.entity.UserInfo;
import com.itil.enums.TicketCategory;

import java.util.List;

public class TeamMemberDTO extends UserInfo {
    private int maxTicketsAllowed = 5;

    private TicketCategory ticketCategory;
    private List<Ticket> assignedTickets;
    private Gatekeeper gatekeeper;

    public TeamMemberDTO() {
    }

    public List<Ticket> getAssignedTickets() {
        return assignedTickets;
    }

    public void setAssignedTickets(List<Ticket> assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public Gatekeeper getGatekeeper() {
        return gatekeeper;
    }

    public void setGatekeeper(Gatekeeper gatekeeper) {
        this.gatekeeper = gatekeeper;
    }

    public int getMaxTicketsAllowed() {
        return maxTicketsAllowed;
    }

    public void setMaxTicketsAllowed(int maxTicketsAllowed) {
        this.maxTicketsAllowed = maxTicketsAllowed;
    }
}
