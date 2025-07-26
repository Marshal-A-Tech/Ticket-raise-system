package com.itil.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import jakarta.persistence.*;


@Entity
@Table(name = "ticket")
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	private TicketCategory ticketCategory;
	private String title;
	private String description;
	@Enumerated(EnumType.STRING)
	private TicketStatus status;
	@Enumerated(EnumType.STRING)
	private PriorityLevel priority;
	private LocalDateTime createdAt;
	@ManyToOne
	@JoinColumn(name = "raised_by")
	@JsonIgnore
	private User raisedBy;
	@ManyToOne
	@JoinColumn(name = "assigned_to")
	private TeamMember assignedTo;
	private LocalDateTime updatedAt;
	private LocalDateTime resolvedAt;
	private String resolutionDetails;
	@ManyToOne
	@JoinColumn(name = "training_id")
	private Training training;
	@ManyToOne
	@JoinColumn(name = "gatekeeper_id")
	private Gatekeeper gatekeeper;

	public Ticket() {

	}

	public Ticket(Long id, TicketCategory ticketCategory, String title, String description, TicketStatus status, PriorityLevel priority, LocalDateTime createdAt, User raisedBy, TeamMember assignedTo, LocalDateTime updatedAt, LocalDateTime resolvedAt, String resolutionDetails, Training training, Gatekeeper gatekeeper) {
		this.id = id;
		this.ticketCategory = ticketCategory;
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.createdAt = createdAt;
		this.raisedBy = raisedBy;
		this.assignedTo = assignedTo;
		this.updatedAt = updatedAt;
		this.resolvedAt = resolvedAt;
		this.resolutionDetails = resolutionDetails;
		this.training = training;
		this.gatekeeper = gatekeeper;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TicketCategory getTicketCategory() {
		return ticketCategory;
	}

	public void setTicketCategory(TicketCategory ticketCategory) {
		this.ticketCategory = ticketCategory;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketStatus getTicketStatus() {
		return status;
	}

	public void setTicketStatus(TicketStatus status) {
		this.status = status;
	}

	public PriorityLevel getPriorityLevel() {
		return priority;
	}

	public void setPriorityLevel(PriorityLevel priority) {
		this.priority = priority;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(User raisedBy) {
		this.raisedBy = raisedBy;
	}

	public TeamMember getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(TeamMember assignedTo) {
		this.assignedTo = assignedTo;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

	public void setResolvedAt(LocalDateTime resolvedAt) {
		this.resolvedAt = resolvedAt;
	}

	public String getResolutionDetails() {
		return resolutionDetails;
	}

	public void setResolutionDetails(String resolutionDetails) {
		this.resolutionDetails = resolutionDetails;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public Gatekeeper getGatekeeper() {
		return gatekeeper;
	}

	public void setGatekeeper(Gatekeeper gatekeeper) {
		this.gatekeeper = gatekeeper;
	}
}
