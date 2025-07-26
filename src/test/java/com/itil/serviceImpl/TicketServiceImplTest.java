package com.itil.serviceImpl;

import com.itil.dto.*;
import com.itil.entity.Gatekeeper;
import com.itil.entity.Ticket;
import com.itil.entity.User;
import com.itil.enums.PriorityLevel;
import com.itil.enums.Role;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TicketRepository;
import com.itil.repository.UserRepository;
import com.itil.serviceimpl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private GatekeeperRepository gatekeeperRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImplTest.class);

    private User mockUser;
    private Gatekeeper mockGatekeeper;
    private RaiseTicketDTO raiseTicketDTO;
    private Ticket savedTicket;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Priya");
        mockUser.setEmail("priya@example.com");
        mockGatekeeper = new Gatekeeper();
        mockGatekeeper.setId(1L);
        mockGatekeeper.setName("Gatekeeper 1");
        raiseTicketDTO = new RaiseTicketDTO();
        raiseTicketDTO.setTicketCategory(TicketCategory.SOFTWARE);
        raiseTicketDTO.setTitle("System Crash");
        raiseTicketDTO.setDescription("System crashes frequently.");
        raiseTicketDTO.setPriorityLevel(PriorityLevel.HIGH);

        savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setTicketCategory(raiseTicketDTO.getTicketCategory());
        savedTicket.setTitle(raiseTicketDTO.getTitle());
        savedTicket.setDescription(raiseTicketDTO.getDescription());
        savedTicket.setCreatedAt(LocalDateTime.now());
        savedTicket.setRaisedBy(mockUser);
        savedTicket.setPriorityLevel(raiseTicketDTO.getPriorityLevel());
        savedTicket.setTicketStatus(TicketStatus.OPEN);
        savedTicket.setGatekeeper(mockGatekeeper);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setTicketCategory(TicketCategory.SOFTWARE);
        ticket.setPriorityLevel(PriorityLevel.MEDIUM);
        ticket.setTicketStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testRaiseTicket_Success() {
        User user = new User();
        user.setName("Priya");
        user.setEmail("priya@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);
        user.setCertificateName(null);
        user.setCertificateIssueDate(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(gatekeeperRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(mockGatekeeper));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(modelMapper.map(any(Ticket.class), eq(RaiseTicketResponseDTO.class)))
                .thenReturn(new RaiseTicketResponseDTO(1L, TicketCategory.SOFTWARE, "System Crash", "Not Working", TicketStatus.OPEN, PriorityLevel.HIGH, LocalDateTime.now(), user));
        ApiResponse<RaiseTicketResponseDTO> response = ticketService.raiseTicket(1L, raiseTicketDTO);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Ticket Raised Successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getTicketId());
        assertEquals(TicketCategory.SOFTWARE, response.getData().getTicketCategory());
        verify(userRepository, times(1)).findById(1L);
        verify(gatekeeperRepository, times(1)).findFirstByOrderByIdAsc();
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testRaiseTicket_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.raiseTicket(1L, raiseTicketDTO)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(gatekeeperRepository, never()).findFirstByOrderByIdAsc();
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testRaiseTicket_GatekeeperNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(gatekeeperRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.raiseTicket(1L, raiseTicketDTO)
        );
        assertEquals("No Gatekeeper found.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(gatekeeperRepository, times(1)).findFirstByOrderByIdAsc();
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testGetAllTicketsByPriority_ValidPriorityWithTickets() {
        PriorityLevel priorityLevel = PriorityLevel.HIGH;
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findByPriority(priorityLevel)).thenReturn(tickets);
        ApiResponse<List<PriorityResponseDTO>> response = ticketService.getAllTicketsByPriority(priorityLevel);
        assertNotNull(response);
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetAllTicketsByPriority_ValidPriorityNoTickets() {
        PriorityLevel priorityLevel = PriorityLevel.LOW;
        when(ticketRepository.findByPriority(priorityLevel)).thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getAllTicketsByPriority(priorityLevel);
        });
        assertEquals("No tickets found for priority: LOW", exception.getMessage());
    }

    @Test
    void testGetTicketsByCategory_ValidCategoryWithTickets() {
        TicketCategory ticketCategory = TicketCategory.SOFTWARE;
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findByTicketCategory(ticketCategory)).thenReturn(tickets);
        ApiResponse<List<PriorityResponseDTO>> response = ticketService.getTicketsByCategory(ticketCategory);
        assertNotNull(response);
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetTicketsByCategory_ValidCategoryNoTickets() {
        TicketCategory ticketCategory = TicketCategory.TRAINING;
        when(ticketRepository.findByTicketCategory(ticketCategory)).thenReturn(Collections.emptyList());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getTicketsByCategory(ticketCategory);
        });
        assertEquals("No tickets found for category: TRAINING", exception.getMessage());
    }

    @Test
    void testGetCountOfTicketsByCategory_TicketsExist() {
        List<Ticket> tickets = List.of(ticket, ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);
        ApiResponse<Map<TicketCategory, Long>> response = ticketService.getCountOfTicketsByCategory();
        assertNotNull(response);
        assertEquals(2, response.getData().get(TicketCategory.SOFTWARE));
    }

    @Test
    void testGetCountOfTicketsByCategory_NoTickets() {
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getCountOfTicketsByCategory();
        });
        assertEquals("No tickets found in the system.", exception.getMessage());
    }

    @Test
    void testGetAllTickets_TicketsExist() {
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);
        ApiResponse<List<PriorityResponseDTO>> response = ticketService.getAllTickets();
        assertNotNull(response);
        assertEquals(1, response.getData().size());
    }

    @Test
    void testGetAllTickets_NoTickets() {
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getAllTickets();
        });
        assertEquals("No tickets found in the system.", exception.getMessage());
    }


    @Test
    void testGetAllTicketsByPriority_NullPriority() {
        assertThrows(ResourceNotFoundException.class, () -> ticketService.getAllTicketsByPriority(null));
    }
    @Test
    void testGetTicketsByCategory_NullCategory() {
        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketsByCategory(null));
    }

    @Test
    void testGetCountOfTicketsByCategory_DatabaseConnectionFailure() {
        when(ticketRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getCountOfTicketsByCategory();
        });
        assertEquals("Database connection failed", exception.getMessage());
    }

    @Test
    void testGetAllTickets_DatabaseConnectionFailure() {
        when(ticketRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getAllTickets();
        });
        assertEquals("Database connection failed", exception.getMessage());
    }

    @Test
    void testUpdateTicket_ValidUpdate() {
        UpdateTicketUserRequest request = new UpdateTicketUserRequest();
        request.setTicketStatus(TicketStatus.COMPLETED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        UpdateTicketUserResponseDTO responseDTO = new UpdateTicketUserResponseDTO();
        responseDTO.setTicketStatus(TicketStatus.COMPLETED);
        when(modelMapper.map(any(Ticket.class), eq(UpdateTicketUserResponseDTO.class))).thenReturn(responseDTO);
        ApiResponse<UpdateTicketUserResponseDTO> response = ticketService.updateTicket(1L, request);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Ticket updated successfully.", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(TicketStatus.COMPLETED, response.getData().getTicketStatus());
    }

    @Test
    void testUpdateTicket_TicketNotFound() {
        UpdateTicketUserRequest request = new UpdateTicketUserRequest();
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.updateTicket(1L, request);
        });
        assertEquals("Ticket not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetTicketsByStatus_ValidStatus() {
        when(ticketRepository.findByStatus(TicketStatus.OPEN)).thenReturn(Collections.singletonList(ticket));
        List<TicketStatusDTO> tickets = ticketService.getTicketsByStatus(TicketStatus.OPEN);
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
    }

    @Test
    void testGetTicketsByStatus_NoTicketsFound() {
        when(ticketRepository.findByStatus(TicketStatus.OPEN)).thenReturn(Collections.emptyList());
        List<TicketStatusDTO> tickets = ticketService.getTicketsByStatus(TicketStatus.OPEN);
        assertNotNull(tickets);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void testGetAllTicketsByRaisedById_ValidUserId() {
        when(ticketRepository.findByRaisedById(1)).thenReturn(Collections.singletonList(ticket));
        List<TicketStatusDTO> tickets = ticketService.getAllTicketsByRaisedById(1);
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
    }

    @Test
    void testGetAllTicketsByRaisedById_NoTicketsFound() {
        when(ticketRepository.findByRaisedById(1)).thenReturn(Collections.emptyList());
        List<TicketStatusDTO> tickets = ticketService.getAllTicketsByRaisedById(1);
        assertNotNull(tickets);
        assertTrue(tickets.isEmpty());
    }
}

