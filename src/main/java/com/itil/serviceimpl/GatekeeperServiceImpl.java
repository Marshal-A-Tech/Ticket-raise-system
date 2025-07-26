package com.itil.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.itil.dto.*;
import com.itil.entity.Gatekeeper;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.ResourceNotFoundException;
import com.itil.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itil.entity.TeamMember;
import com.itil.entity.Ticket;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.TicketRepository;
import com.itil.service.GatekeeperService;

@Service
public class GatekeeperServiceImpl implements GatekeeperService {
    private static final Logger logger = LoggerFactory.getLogger(GatekeeperServiceImpl.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private GatekeeperRepository gatekeeperRepository;

    @Override

    public ApiResponse<TicketDTO> assignTicket(Long ticketId, Long teamMemberId) {
        logger.info("Assigning ticket: " + ticketId + " to team member " + teamMemberId);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.error("Ticket not found with ID: " +ticketId);
                    return new ResourceNotFoundException("Ticket not found with ID: " + ticketId);
                });
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> {
                    logger.error("Team Member not found with ID: " + teamMemberId);
                    return new ResourceNotFoundException("Team Member not found with ID: " + teamMemberId);
                });
        if (teamMember.getAssignedTickets().size() >= teamMember.getMaxTicketsAllowed()) {
            logger.warn("Team Member ID: {} already has the maximum number of assigned tickets", teamMemberId);
            throw new ResourceNotFoundException("Team Member with ID: " + teamMemberId + " already has the maximum number of assigned tickets.");
        }
        if (!teamMember.getTicketCategory().equals(ticket.getTicketCategory())){
            throw new ResourceNotFoundException("Team Member is not suitable for this category.");
        }
        ticket.setAssignedTo(teamMember);
        ticket.setTicketStatus(TicketStatus.ASSIGNED);
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        logger.info("Successfully assigned Ticket ID: " + ticketId + " to Team Member ID: " + teamMemberId);
        TicketDTO response = mapToDto(updatedTicket);
        return ResponseUtil.success(response, "Successfully Assigned ticket to team member");
    }

    @Override
    public ApiResponse<List<TicketDTO>> getTicketsByTeamMemberId(Long teamMemberId) {
        logger.info("Fetching tickets assigned to Team Member ID: "+ teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> {
                    logger.error("Team Member not found with ID: "+ teamMemberId);
                    return new ResourceNotFoundException("Team Member not found with ID: " + teamMemberId);
                });

        List<Ticket> tickets = ticketRepository.findByAssignedToId(teamMemberId);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for Team Member ID: "+ teamMemberId);
        }
        List<TicketDTO> response = tickets.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseUtil.success(response, "Successfully fetched the tickets assigned to the Team Member Id: "+teamMemberId);
    }

    @Override
    public ApiResponse<TeamMemberDTO> getTeamMemberById(Long teamMemberId) {
        logger.info("Fetching details of Team Member ID: "+ teamMemberId);

        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> {
                    logger.error("Team Member not found with ID: "+ teamMemberId);
                    return new ResourceNotFoundException("Team Member not found with ID: " + teamMemberId);
                });

        logger.info("Successfully retrieved details for Team Member ID: "+ teamMemberId);
        TeamMemberDTO response = mapToDto(teamMember);
        return ResponseUtil.success(response, "Successfully retrived the details of Team Member ID: "+ teamMemberId);
    }

    private TeamMemberDTO mapToDto(TeamMember teamMember) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        teamMemberDTO.setId(teamMember.getId());
        teamMemberDTO.setName(teamMember.getName());
        teamMemberDTO.setEmail(teamMember.getEmail());
        teamMemberDTO.setRole(teamMember.getRole());
        teamMemberDTO.setMaxTicketsAllowed(teamMember.getMaxTicketsAllowed());
        return teamMemberDTO;
    }

    @Override
    public ApiResponse<List<TeamMemberResponseDTO>> getTeamMembersByTicketCategory(TicketCategory ticketCategory) {
        logger.info("Fetching Team Members specialized in Category: "+ ticketCategory);
        if(ticketCategory == null) {
            logger.error("Ticket Category is required but not provided.");
            throw new ResourceNotFoundException("Ticket Category is required");
        }
        boolean categoryExists = ticketRepository.existsByTicketCategory(ticketCategory);
        if(!categoryExists) {
            logger.error("Ticket Category {} does not exist in database.", ticketCategory);
            throw new ResourceNotFoundException("Ticket Category '" + ticketCategory + "' does not exist.");
        }
        List<TeamMember> teamMembers = teamMemberRepository.findByTicketCategory(ticketCategory);
        if (teamMembers.isEmpty()) {
            logger.warn("No Team Members found for Category: " +  ticketCategory);
        }
        List<TeamMemberResponseDTO> response = teamMembers.stream().map(this::maptoResponseDTO).collect(Collectors.toList());
        return ResponseUtil.success(response, "Team Members By Ticket Category");
    }



    private TeamMemberResponseDTO maptoResponseDTO(TeamMember teamMember) {
        TeamMemberResponseDTO teamMemberResponseDTO = new TeamMemberResponseDTO();

        teamMemberResponseDTO.setId(teamMember.getId());
        teamMemberResponseDTO.setName(teamMember.getName());
        teamMemberResponseDTO.setEmail(teamMember.getEmail());
        teamMemberResponseDTO.setRole(teamMember.getRole());
        teamMemberResponseDTO.setMaxTicketsAllowed(teamMember.getMaxTicketsAllowed());
        teamMemberResponseDTO.setGatekeeper(teamMember.getGatekeeper());
        return teamMemberResponseDTO;
    }

    private TicketDTO mapToDto(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();

        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setTicketStatus(ticket.getTicketStatus());
        ticketDTO.setTicketCategory(ticket.getTicketCategory());
        ticketDTO.setPriorityLevel(ticket.getPriorityLevel());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        ticketDTO.setUpdatedAt(ticket.getUpdatedAt());
        ticketDTO.setResolvedAt(ticket.getResolvedAt());
        ticketDTO.setResolutionDetails(ticket.getResolutionDetails());
        return ticketDTO;
    }

    @Override
    public ApiResponse<String> assignGateKeeper(Long teamMemberId, Long gatekeeperId) {
        logger.info("Assigning Gatekeeper: " + gatekeeperId + " to TeamMember: " + teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> {
                    logger.error("TeamMember not found with ID: "+ teamMemberId);
                    return new ResourceNotFoundException("TeamMember not found");
                });
        Gatekeeper gatekeeper = gatekeeperRepository.findById(gatekeeperId)
                .orElseThrow(() -> {
                    logger.error("Gatekeeper not found with ID: "+ gatekeeperId);
                    return new ResourceNotFoundException("Gatekeeper not found");
                });
        teamMember.setGatekeeper(gatekeeper);
        teamMemberRepository.save(teamMember);
        logger.info("Successfully assigned Gatekeeper: "+ gatekeeperId + " to TeamMember: "+ teamMemberId);
        return ResponseUtil.success(teamMember.getName(), "Gatekeeper assigned successfully");
    }

}