package com.itil.dto;

import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;

public class PriorityResponseDTO {
    private Long id;
    private TicketCategory ticketCategory;
    private String title;
    private String description;
    private TicketStatus ticketStatus;
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
        return ticketStatus;
    }
    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
