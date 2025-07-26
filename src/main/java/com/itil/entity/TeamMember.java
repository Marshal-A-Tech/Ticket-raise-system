package com.itil.entity;

import java.util.List;

import com.itil.enums.Role;
import com.itil.enums.TicketCategory;
import jakarta.persistence.*;


@Entity
@Table(name = "team_member")
@DiscriminatorValue("TEAM_MEMBER")
public class TeamMember extends UserInfo{
	private int maxTicketsAllowed = 5;

	@Enumerated(EnumType.STRING)
	private TicketCategory ticketCategory;

	@OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
	private List<Ticket> assignedTickets;

	@ManyToOne
	@JoinColumn(name = "gatekeeper_id")
	private Gatekeeper gatekeeper;

	public TeamMember() {

	}

	public TeamMember(Long id, String name, String email, String password, Role role, int maxTicketsAllowed, TicketCategory ticketCategory, List<Ticket> assignedTickets, Gatekeeper gatekeeper) {
		super(id, name, email, password, role);
		this.maxTicketsAllowed = maxTicketsAllowed;
		this.ticketCategory = ticketCategory;
		this.assignedTickets = assignedTickets;
		this.gatekeeper = gatekeeper;
	}

	public int getMaxTicketsAllowed() {
		return maxTicketsAllowed;
	}

	public void setMaxTicketsAllowed(int maxTicketsAllowed) {
		this.maxTicketsAllowed = maxTicketsAllowed;
	}

	public TicketCategory getTicketCategory() {
		return ticketCategory;
	}

	public void setTicketCategory(TicketCategory ticketCategory) {
		this.ticketCategory = ticketCategory;
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
}