package com.itil.serviceimpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.itil.dto.*;
import com.itil.entity.Gatekeeper;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.util.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itil.entity.TeamMember;
import com.itil.entity.Ticket;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketStatus;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.TicketRepository;
import com.itil.service.TeamMemberService;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    private static final Logger logger = LoggerFactory.getLogger(TeamMemberServiceImpl.class);


    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private GatekeeperRepository gatekeeperRepository;

    @Autowired
    private ModelMapper modelMapper;


    public ApiResponse<List<RaiseTicketResponseDTO>> getAssignedTicketsById(Long teamMemberId) {
        logger.info("Fetching assigned tickets for Team Member ID: " + teamMemberId);
        List<Ticket> tickets = ticketRepository.findByAssignedToId(teamMemberId);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for Team Member ID: " + teamMemberId);
            return ResponseUtil.success(Collections.emptyList(), "No tickets assigned to this team member");
        }
        List<RaiseTicketResponseDTO> finalTickets = tickets.stream().map(ticket -> modelMapper.map(ticket, RaiseTicketResponseDTO.class)).collect(Collectors.toList());
        logger.info("Assigned tickets fetched successfully for Team Member ID: " + teamMemberId);
        return ResponseUtil.success(finalTickets, "Assigned tickets retrieved successfully");
    }

    @Override

    public ApiResponse<UpdateTicketResponseDTO> updateTicketStatus(Long ticketId, Long teamMemberId, UpdateTicketRequest updateTicketRequest) {
        logger.info("Updating ticket status: " + ticketId + " , " + teamMemberId);
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        if (!ticket.getAssignedTo().getId().equals(teamMemberId)) {
            logger.warn("Unauthorized attempt to update Ticket ID: " + ticketId + " by Team Member ID: " + teamMemberId);
            return ResponseUtil.success(null, "You are not assigned to this ticket.");
        }
        ticket.setTicketStatus(updateTicketRequest.getTicketStatus());
        ticket.setResolutionDetails(updateTicketRequest.getResolutionDetails());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setResolvedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        UpdateTicketResponseDTO response = modelMapper.map(ticket, UpdateTicketResponseDTO.class);
        logger.info("Ticket ID: " + ticketId + " status updated successfully");
        return ResponseUtil.success(response, "Ticket updated successfully.");
    }


    @Override
    public ApiResponse<List<RaiseTicketResponseDTO>> getAssignedTicketsByPriority(Long teamMemberId, PriorityLevel priorityLevel) {
        logger.info("Fetching assigned tickets for Team Member ID: " + teamMemberId + " with Priority Level: " + priorityLevel);
        List<Ticket> tickets = ticketRepository.findByAssignedToIdAndPriority(teamMemberId, priorityLevel);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for Team Member ID: " + teamMemberId + " with Priority Level: " + priorityLevel);
            return ResponseUtil.success(Collections.emptyList(), "No tickets with the specified priority level");
        }
        List<RaiseTicketResponseDTO> response = tickets.stream().map(ticket -> modelMapper.map(ticket, RaiseTicketResponseDTO.class)).collect(Collectors.toList());
        logger.info("Assigned tickets with priority fetched successfully for Team Member ID: " + teamMemberId);
        return ResponseUtil.success(response, "Assigned tickets retrieved successfully");
    }
}


