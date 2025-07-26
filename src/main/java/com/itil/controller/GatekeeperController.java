package com.itil.controller;

import com.itil.dto.*;
import com.itil.enums.TicketCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.itil.service.GatekeeperService;

import java.util.List;

@RestController
@RequestMapping("/api/gatekeeper")
public class GatekeeperController {

    private static final Logger logger = LoggerFactory.getLogger(GatekeeperController.class);
    @Autowired
    private GatekeeperService gatekeeperService;

    @PostMapping("/tickets/{ticketId}/assignees/{teamMemberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<TicketDTO>> assignTicket(
            @PathVariable Long ticketId, @PathVariable Long teamMemberId) {
        logger.info("Received request to assign ticket ID: " + ticketId + " to team member ID:  " + teamMemberId);
        ApiResponse<TicketDTO> ticketDTO = gatekeeperService.assignTicket(ticketId, teamMemberId);
        return ResponseEntity.ok(ticketDTO);
    }

    @GetMapping("/teamMembers/{teamMemberId}/tickets")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getTicketsByTeamMemberId(@PathVariable Long teamMemberId) {
        logger.info("Fetching tickets assigned to team member ID: "+ teamMemberId);
        ApiResponse<List<TicketDTO>> tickets = gatekeeperService.getTicketsByTeamMemberId(teamMemberId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/teamMembers/{teamMemberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> getTeamMemberById(@PathVariable Long teamMemberId) {
        logger.info("Fetching details of team member ID: "+ teamMemberId);
        ApiResponse<TeamMemberDTO> teamMemberDTO = gatekeeperService.getTeamMemberById(teamMemberId);
        return ResponseEntity.ok(teamMemberDTO);
    }

    @GetMapping("/teamMembers/category/{ticketCategory}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<List<TeamMemberResponseDTO>> > getTeamMembersByTicketCategory(
            @PathVariable TicketCategory ticketCategory) {
        logger.info("Fetching team members for ticket category: " + ticketCategory);
        ApiResponse<List<TeamMemberResponseDTO>>  teamMembers = gatekeeperService.getTeamMembersByTicketCategory(ticketCategory);
        return ResponseEntity.ok(teamMembers);
    }

    @PutMapping("/{teamMemberId}/gatekeeper/{gatekeeperId}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<String>> assignGatekeeper(
            @PathVariable Long teamMemberId,
            @PathVariable Long gatekeeperId) {
        logger.info("Assigning gatekeeper: " + gatekeeperId + " , to Team Member ID: "+ teamMemberId);
        ApiResponse<String> response = gatekeeperService.assignGateKeeper(teamMemberId, gatekeeperId);
        logger.info("Gatekeeper Assigned successfully: " + teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
