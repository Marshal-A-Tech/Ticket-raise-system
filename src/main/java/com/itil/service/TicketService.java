package com.itil.service;

import com.itil.dto.*;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;

import java.util.List;
import java.util.Map;

public interface TicketService {

    ApiResponse<RaiseTicketResponseDTO> raiseTicket(Long userId, RaiseTicketDTO raiseTicketDTO);
    ApiResponse<UpdateTicketUserResponseDTO> updateTicket(Long ticketId, UpdateTicketUserRequest updateTicketUserRequest);

    List<TicketStatusDTO> getTicketsByStatus(TicketStatus ticketStatus);

    ApiResponse<List<PriorityResponseDTO>> getAllTicketsByPriority(PriorityLevel priority);

    ApiResponse<List<PriorityResponseDTO>> getTicketsByCategory(TicketCategory ticketCategory);

    ApiResponse<Map<TicketCategory, Long>> getCountOfTicketsByCategory();

    ApiResponse<List<PriorityResponseDTO>> getAllTickets();

    List<TicketStatusDTO> getAllTicketsByRaisedById(Integer raisedById);
}
