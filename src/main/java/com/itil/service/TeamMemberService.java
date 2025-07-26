package com.itil.service;

import java.util.List;

import com.itil.dto.ApiResponse;
import com.itil.dto.RaiseTicketResponseDTO;
import com.itil.dto.UpdateTicketRequest;
import com.itil.dto.UpdateTicketResponseDTO;
import com.itil.enums.PriorityLevel;

public interface TeamMemberService {
    ApiResponse<List<RaiseTicketResponseDTO>> getAssignedTicketsById(Long teamMemberId);


    ApiResponse<UpdateTicketResponseDTO> updateTicketStatus(Long ticketId, Long teamMemberId, UpdateTicketRequest updateTicketRequest);


    ApiResponse<List<RaiseTicketResponseDTO>> getAssignedTicketsByPriority(Long teamMemberId, PriorityLevel priorityLevel);
}
