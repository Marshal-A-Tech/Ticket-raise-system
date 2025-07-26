package com.itil.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTicketUserResponseDTO {
    @JsonProperty("ticketId")
    private Long ticketId;
    @JsonProperty("ticketCategory")
    private TicketCategory ticketCategory;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("ticketStatus")
    private TicketStatus ticketStatus;
    @JsonProperty("priorityLevel")
    private PriorityLevel priorityLevel;
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

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
}

