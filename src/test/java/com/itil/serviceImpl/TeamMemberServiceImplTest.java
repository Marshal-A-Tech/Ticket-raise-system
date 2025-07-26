package com.itil.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
 
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
 
import com.itil.dto.ApiResponse;
import com.itil.dto.RaiseTicketResponseDTO;
import com.itil.dto.UpdateTicketRequest;
import com.itil.dto.UpdateTicketResponseDTO;
import com.itil.entity.Gatekeeper;
import com.itil.entity.TeamMember;
import com.itil.entity.Ticket;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketStatus;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.TicketRepository;
import com.itil.serviceimpl.TeamMemberServiceImpl;
 
@ExtendWith(MockitoExtension.class)
class TeamMemberServiceImplTest {
 
    @Mock
    private TeamMemberRepository teamMemberRepository;
 
    @Mock
    private TicketRepository ticketRepository;
 
    @Mock
    private GatekeeperRepository gatekeeperRepository;
 
    @Mock
    private ModelMapper modelMapper;
 
    @InjectMocks
    private TeamMemberServiceImpl teamMemberService;
 
    private Long teamMemberId;
    private Long ticketId;
    private Long gatekeeperId;
    private TeamMember teamMember;
    private Ticket ticket;
    private Gatekeeper gatekeeper;
 
    @BeforeEach
    void setUp() {
        teamMemberId = 1L;
        ticketId = 10L;
        gatekeeperId = 20L;
 
        teamMember = new TeamMember();
        teamMember.setId(teamMemberId);
        teamMember.setName("Test Team Member");
 
        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setAssignedTo(teamMember);
        ticket.setTicketStatus(TicketStatus.OPEN);
 
        gatekeeper = new Gatekeeper();
        gatekeeper.setId(gatekeeperId);
        gatekeeper.setName("Test Gatekeeper");
    }
 
    @Test
    void getAssignedTicketsById_Positive() {
        when(ticketRepository.findByAssignedToId(teamMemberId)).thenReturn(Arrays.asList(ticket));
        when(modelMapper.map(ticket, RaiseTicketResponseDTO.class)).thenReturn(new RaiseTicketResponseDTO());
 
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsById(teamMemberId);
 
        assertTrue(response.isSuccess());
        assertEquals("Assigned tickets retrieved successfully", response.getMessage());
        assertFalse(response.getData().isEmpty());
    }
 
    @Test
    void getAssignedTicketsById_Negative_NoTicketsFound() {
        when(ticketRepository.findByAssignedToId(teamMemberId)).thenReturn(Collections.emptyList());
 
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsById(teamMemberId);
 
        assertTrue(response.isSuccess());
        assertEquals("No tickets assigned to this team member", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }
 
    @Test
    void updateTicketStatus_Positive() {
        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setTicketStatus(TicketStatus.COMPLETED);
        request.setResolutionDetails("Resolved");
 
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(modelMapper.map(ticket, UpdateTicketResponseDTO.class)).thenReturn(new UpdateTicketResponseDTO());
 
        ApiResponse<UpdateTicketResponseDTO> response = teamMemberService.updateTicketStatus(ticketId, teamMemberId, request);
 
        assertTrue(response.isSuccess());
        assertEquals("Ticket updated successfully.", response.getMessage());
    }
 
    @Test
    void updateTicketStatus_Negative_TicketNotFound() {
        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setTicketStatus(TicketStatus.COMPLETED);
        request.setResolutionDetails("Resolved");
 
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> teamMemberService.updateTicketStatus(ticketId, teamMemberId, request));
    }
 
    @Test
    void updateTicketStatus_Negative_Unauthorized() {
        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setTicketStatus(TicketStatus.COMPLETED);
        request.setResolutionDetails("Resolved");
 
        TeamMember otherTeamMember = new TeamMember();
        otherTeamMember.setId(2L);
        ticket.setAssignedTo(otherTeamMember);
 
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
 
        ApiResponse<UpdateTicketResponseDTO> response = teamMemberService.updateTicketStatus(ticketId, teamMemberId, request);
 
        assertTrue(response.isSuccess());
        assertEquals("You are not assigned to this ticket.", response.getMessage());
        assertTrue(Objects.isNull(response.getData()));
    }
 
    @Test
    void getAssignedTicketsByPriority_Positive() {
        when(ticketRepository.findByAssignedToIdAndPriority(teamMemberId, PriorityLevel.HIGH)).thenReturn(Arrays.asList(ticket));
        when(modelMapper.map(ticket, RaiseTicketResponseDTO.class)).thenReturn(new RaiseTicketResponseDTO());
 
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsByPriority(teamMemberId, PriorityLevel.HIGH);
 
        assertTrue(response.isSuccess());
        assertEquals("Assigned tickets retrieved successfully", response.getMessage());
        assertFalse(response.getData().isEmpty());
    }
 
    @Test
    void getAssignedTicketsByPriority_Negative_NoTicketsFound() {
        when(ticketRepository.findByAssignedToIdAndPriority(teamMemberId, PriorityLevel.HIGH)).thenReturn(Collections.emptyList());
 
        ApiResponse<List<RaiseTicketResponseDTO>> response = teamMemberService.getAssignedTicketsByPriority(teamMemberId, PriorityLevel.HIGH);
 
        assertTrue(response.isSuccess());
        assertEquals("No tickets with the specified priority level", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

}