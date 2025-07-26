package com.itil.dto;

import com.itil.enums.TicketStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketRequest {

    @NotNull(message = "Ticket status should not be null")
    private TicketStatus ticketStatus;

    @NotEmpty(message = "Resolution Details should not be empty")
    private String resolutionDetails;

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }
}
