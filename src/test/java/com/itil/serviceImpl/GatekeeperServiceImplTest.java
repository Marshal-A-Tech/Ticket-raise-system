package com.itil.serviceImpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.TeamMemberDTO;
import com.itil.dto.TeamMemberResponseDTO;
import com.itil.dto.TicketDTO;
import com.itil.entity.Gatekeeper;
import com.itil.entity.TeamMember;
import com.itil.entity.Ticket;
import com.itil.enums.PriorityLevel;
import com.itil.enums.Role;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.TicketRepository;
import com.itil.serviceimpl.GatekeeperServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GatekeeperServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private GatekeeperRepository gatekeeperRepository;

    @InjectMocks
    private GatekeeperServiceImpl gatekeeperService;

    private Ticket ticket;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setTicketCategory(TicketCategory.SOFTWARE);
        ticket.setPriorityLevel(PriorityLevel.MEDIUM);
        ticket.setCreatedAt(LocalDateTime.now());

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setName("Test Member");
        teamMember.setEmail("test@example.com");
        teamMember.setRole(Role.ROLE_TEAMMEMBER);
        teamMember.setMaxTicketsAllowed(5);
        teamMember.setTicketCategory(TicketCategory.SOFTWARE);
        teamMember.setAssignedTickets(Collections.emptyList()); // Initialize the list
    }

    @Test
    void testAssignTicket_ValidAssignment() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ApiResponse<TicketDTO> response = gatekeeperService.assignTicket(1L, 1L);

        assertNotNull(response);
        assertEquals("Successfully Assigned ticket to team member", response.getMessage());
        assertEquals(TicketStatus.ASSIGNED, response.getData().getTicketStatus());
    }

    @Test
    void testAssignTicket_TicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.assignTicket(1L, 1L);
        });

        assertEquals("Ticket not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignTicket_TeamMemberNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.assignTicket(1L, 1L);
        });

        assertEquals("Team Member not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignTicket_MaxTicketsExceeded() {
        teamMember.setAssignedTickets(Collections.nCopies(5, new Ticket()));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.assignTicket(1L, 1L);
        });

        assertEquals("Team Member with ID: 1 already has the maximum number of assigned tickets.", exception.getMessage());
    }

    @Test
    void testAssignTicket_CategoryMismatch() {
        ticket.setTicketCategory(TicketCategory.HARDWARE);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.assignTicket(1L, 1L);
        });

        assertEquals("Team Member is not suitable for this category.", exception.getMessage());
    }

    @Test
    void testGetTicketsByTeamMemberId_ValidTeamMember() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));
        when(ticketRepository.findByAssignedToId(1L)).thenReturn(Collections.singletonList(ticket));

        ApiResponse<List<TicketDTO>> response = gatekeeperService.getTicketsByTeamMemberId(1L);

        assertNotNull(response);
        assertEquals("Successfully fetched the tickets assigned to the Team Member Id: 1", response.getMessage());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void testGetTicketsByTeamMemberId_TeamMemberNotFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.getTicketsByTeamMemberId(1L);
        });

        assertEquals("Team Member not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetTicketsByTeamMemberId_NoTicketsFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));
        when(ticketRepository.findByAssignedToId(1L)).thenReturn(Collections.emptyList());

        ApiResponse<List<TicketDTO>> response = gatekeeperService.getTicketsByTeamMemberId(1L);

        assertNotNull(response);
        assertEquals("Successfully fetched the tickets assigned to the Team Member Id: 1", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetTeamMemberById_ValidTeamMember() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));

        ApiResponse<TeamMemberDTO> response = gatekeeperService.getTeamMemberById(1L);

        assertNotNull(response);
        assertEquals("Successfully retrived the details of Team Member ID: 1", response.getMessage());
        assertEquals("Test Member", response.getData().getName());
    }

    @Test
    void testGetTeamMemberById_TeamMemberNotFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.getTeamMemberById(1L);
        });

        assertEquals("Team Member not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetTeamMembersByTicketCategory_ValidCategory() {
        when(ticketRepository.existsByTicketCategory(TicketCategory.SOFTWARE)).thenReturn(true);
        when(teamMemberRepository.findByTicketCategory(TicketCategory.SOFTWARE)).thenReturn(Collections.singletonList(teamMember));

        ApiResponse<List<TeamMemberResponseDTO>> response = gatekeeperService.getTeamMembersByTicketCategory(TicketCategory.SOFTWARE);

        assertNotNull(response);
        assertEquals("Team Members By Ticket Category", response.getMessage());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void testGetTeamMembersByTicketCategory_CategoryNotProvided() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.getTeamMembersByTicketCategory(null);
        });
        assertEquals("Ticket Category is required", exception.getMessage());
    }

    @Test
    void testGetTeamMembersByTicketCategory_CategoryDoesNotExist() {
        when(ticketRepository.existsByTicketCategory(TicketCategory.SOFTWARE)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gatekeeperService.getTeamMembersByTicketCategory(TicketCategory.SOFTWARE);
        });

        assertEquals("Ticket Category 'SOFTWARE' does not exist.", exception.getMessage());
    }

    @Test
    void testGetTeamMembersByTicketCategory_NoTeamMembersFound() {
        when(ticketRepository.existsByTicketCategory(TicketCategory.SOFTWARE)).thenReturn(true);
        when(teamMemberRepository.findByTicketCategory(TicketCategory.SOFTWARE)).thenReturn(Collections.emptyList());

        ApiResponse<List<TeamMemberResponseDTO>> response = gatekeeperService.getTeamMembersByTicketCategory(TicketCategory.SOFTWARE);

        assertNotNull(response);
        assertEquals("Team Members By Ticket Category", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }
    Long teamMemberId = 1L;
    Long gatekeeperId = 1L;

    @Test
    void assignGateKeeper_Positive() {
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));
        Gatekeeper gatekeeper = new Gatekeeper();
        when(gatekeeperRepository.findById(gatekeeperId)).thenReturn(Optional.of(gatekeeper));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        ApiResponse<String> response = gatekeeperService.assignGateKeeper(teamMemberId, gatekeeperId);

        assertTrue(response.isSuccess());
        assertEquals("Gatekeeper assigned successfully", response.getMessage());
        assertEquals(teamMember.getName(), response.getData());
    }

    @Test
    void assignGateKeeper_Negative_TeamMemberNotFound() {
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gatekeeperService.assignGateKeeper(teamMemberId, gatekeeperId));
    }

    @Test
    void assignGateKeeper_Negative_GatekeeperNotFound() {
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));
        when(gatekeeperRepository.findById(gatekeeperId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gatekeeperService.assignGateKeeper(teamMemberId, gatekeeperId));
    }
}
