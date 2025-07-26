package com.itil.entity;


import java.util.List;

import com.itil.enums.Role;
import jakarta.persistence.*;


@Entity
@Table(name = "gatekeeper")
@DiscriminatorValue("GATEKEEPER")
public class Gatekeeper extends UserInfo{
	@OneToMany(mappedBy = "gatekeeper", cascade = CascadeType.ALL)
	private List<Ticket> assignedTickets;
	@OneToMany(mappedBy = "gatekeeper", cascade = CascadeType.ALL)
	private List<TeamMember> teamMembers;

	public Gatekeeper() {

	}

//	public Gatekeeper(Long id, String name, String email, String password, Role role, List<Ticket> assignedTickets, List<TeamMember> teamMembers) {
//		super(id, name, email, password, role);
//		this.assignedTickets = assignedTickets;
//		this.teamMembers = teamMembers;
//	}

//	public List<Ticket> getAssignedTickets() {
//		return assignedTickets;
//	}
//
//	public void setAssignedTickets(List<Ticket> assignedTickets) {
//		this.assignedTickets = assignedTickets;
//	}
//
//	public List<TeamMember> getTeamMembers() {
//		return teamMembers;
//	}
//
//	public void setTeamMembers(List<TeamMember> teamMembers) {
//		this.teamMembers = teamMembers;
//	}
}
