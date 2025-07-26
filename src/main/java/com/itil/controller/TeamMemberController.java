package com.itil.controller;

import com.itil.dto.ApiResponse;
import com.itil.dto.RaiseTicketResponseDTO;
import com.itil.dto.UpdateTicketRequest;
import com.itil.dto.UpdateTicketResponseDTO;
import com.itil.enums.PriorityLevel;
import com.itil.service.TeamMemberService;

import com.itil.serviceimpl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teamMembers")
public class TeamMemberController {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private TeamMemberService teamMemberService;

    @GetMapping("/{teamMemberId}/tickets")
    @PreAuthorize("hasAuthority('ROLE_TEAMMEMBER')")
    public ResponseEntity<ApiResponse<List<RaiseTicketResponseDTO>>> getAssignedTickets(@PathVariable Long teamMemberId) {
        logger.info("Received request to get assigned tickets for Team Member ID: "+ teamMemberId);
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsById(teamMemberId);
        logger.info("Returning assigned tickets for Team Member ID: " + teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/tickets/{ticketId}/assignees/{teamMemberId}")
    @PreAuthorize("hasAuthority('ROLE_TEAMMEMBER')")
    public ResponseEntity<ApiResponse<UpdateTicketResponseDTO>> updateTicketStatus(
            @PathVariable Long ticketId,
            @PathVariable Long teamMemberId,
            @Valid @RequestBody UpdateTicketRequest updateTicketRequest) {
        logger.info("Received request to update ticket status for Ticket ID: " + ticketId + " , Team Member ID: "+ teamMemberId);
        ApiResponse<UpdateTicketResponseDTO> response = teamMemberService.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest);
        logger.info("Returning response for Ticket ID: " + ticketId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{teamMemberId}/tickets/priority/{priorityLevel}")
    @PreAuthorize("hasAuthority('ROLE_TEAMMEMBER')")
    public ResponseEntity<ApiResponse<List<RaiseTicketResponseDTO>>> getAssignedTicketsByPriority(
            @PathVariable Long teamMemberId,
            @PathVariable PriorityLevel priorityLevel) {
        logger.info("Received request to get assigned tickets by priority for Team Member ID: "+ teamMemberId);
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsByPriority(teamMemberId, priorityLevel);
        logger.info("Returning assigned tickets by priority for Team Member ID: " + teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

