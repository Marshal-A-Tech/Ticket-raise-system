package com.itil.dto;

import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;

import java.time.LocalDateTime;

public class UpdateTicketResponseDTO {

    private Long ticketId;
    private TicketCategory ticketCategory;
    private String title;
    private String description;
    private TicketStatus ticketStatus;
    private PriorityLevel priorityLevel;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolutionDetails;

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setTicketCategory(TicketCategory ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }
}
