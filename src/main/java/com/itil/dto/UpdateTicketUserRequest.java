package com.itil.dto;

import com.itil.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketUserRequest {
    @NotNull(message = "Ticket status should not be null")
    private TicketStatus ticketStatus;

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

}
