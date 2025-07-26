package com.itil.dto;

import com.itil.entity.User;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;

import java.time.LocalDateTime;

public class RaiseTicketResponseDTO {

    private Long ticketId;
    private TicketCategory ticketCategory;
    private String title;
    private String description;

    private TicketStatus ticketStatus;
    private PriorityLevel priorityLevel;
    private LocalDateTime createdAt;
    private User raisedBy;

    public RaiseTicketResponseDTO() {

    }

    public RaiseTicketResponseDTO(Long ticketId, TicketCategory ticketCategory, String title, String description, TicketStatus ticketStatus, PriorityLevel priorityLevel, LocalDateTime createdAt, User raisedBy) {
        this.ticketId = ticketId;
        this.ticketCategory = ticketCategory;
        this.title = title;
        this.description = description;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.createdAt = createdAt;
        this.raisedBy = raisedBy;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(TicketCategory ticketCategory) {
        this.ticketCategory = ticketCategory;
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
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
}
