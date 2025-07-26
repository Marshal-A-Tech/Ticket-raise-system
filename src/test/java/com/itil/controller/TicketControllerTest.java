package com.itil.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itil.dto.*;
import com.itil.entity.User;
import com.itil.enums.Role;
import com.itil.exception.NoTicketFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.itil.controller.TicketController;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.GlobalExceptionHandler;
import com.itil.exception.ResourceNotFoundException;
import com.itil.service.TicketService;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TicketStatus validTicketStatus;
    private TicketStatus invalidTicketStatus;
    private List<TicketDTO> ticketList;
    //private List<TicketStatusDTO> ticketList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validTicketStatus = TicketStatus.OPEN;
        invalidTicketStatus = TicketStatus.CLOSED;
        ticketList = new ArrayList<>();
        TicketDTO ticket = new TicketDTO();

        ticket.setTitle("Test Ticket");
        ticket.setDescription("Description");
        ticket.setTicketStatus(validTicketStatus);

        ticketList.add(ticket);
    }
    @Test
    @WithMockUser(roles = "USER")
    void testRaiseTicket_Success() throws Exception {
        RaiseTicketDTO raiseTicketDTO = new RaiseTicketDTO();
        raiseTicketDTO.setTitle("Test Ticket");
        raiseTicketDTO.setDescription("Issue description");
        raiseTicketDTO.setTitle("Teams");
        raiseTicketDTO.setDescription("Not Working");
        raiseTicketDTO.setTicketCategory(TicketCategory.SOFTWARE);
        raiseTicketDTO.setPriorityLevel(PriorityLevel.HIGH);

        User user = new User();
        user.setName("Priya");
        user.setEmail("priya@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);
        user.setCertificateName(null);
        user.setCertificateIssueDate(null);

        RaiseTicketResponseDTO responseDTO = new RaiseTicketResponseDTO(1L, TicketCategory.SOFTWARE, "Teams", "Not Working", TicketStatus.OPEN, PriorityLevel.HIGH, LocalDateTime.now(), user);

        ApiResponse<RaiseTicketResponseDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(responseDTO);
        apiResponse.setMessage("Ticket Raised Successfully");

        when(ticketService.raiseTicket(anyLong(), any(RaiseTicketDTO.class)))
                .thenReturn(apiResponse);

        mockMvc.perform(post("/api/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(raiseTicketDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Ticket Raised Successfully"))
                .andExpect(jsonPath("$.data.ticketId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRaiseTicket_UserNotFound() throws Exception {
        RaiseTicketDTO raiseTicketDTO = new RaiseTicketDTO();
        raiseTicketDTO.setTitle("Test Ticket");
        raiseTicketDTO.setDescription("Issue description");
        raiseTicketDTO.setTicketCategory(TicketCategory.SOFTWARE);
        raiseTicketDTO.setPriorityLevel(PriorityLevel.HIGH);

        when(ticketService.raiseTicket(anyLong(), any(RaiseTicketDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found with Id: 1"));

        mockMvc.perform(post("/api/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(raiseTicketDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRaiseTicket_InvalidRequest() throws Exception {
        RaiseTicketDTO raiseTicketDTO = new RaiseTicketDTO();
        mockMvc.perform(post("/api/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(raiseTicketDTO)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testUpdateTicket_ValidTicketIdAndDetails() throws Exception {
        Long ticketId = 1L;
        UpdateTicketUserRequest updateTicketUserRequest = new UpdateTicketUserRequest();
        UpdateTicketUserResponseDTO responseDTO = new UpdateTicketUserResponseDTO();
        ApiResponse<UpdateTicketUserResponseDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(responseDTO);
        apiResponse.setMessage("Ticket updated successfully");

        when(ticketService.updateTicket(any(Long.class), any(UpdateTicketUserRequest.class))).thenReturn(apiResponse);

        mockMvc.perform(put("/api/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ticket updated successfully"));
    }

    @Test
    void testUpdateTicket_InvalidTicketId() throws Exception {
        when(ticketService.updateTicket(any(Long.class), any(UpdateTicketUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(put("/api/tickets/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"value\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testUpdateTicket_InvalidUpdateDetails() throws Exception {
        when(ticketService.updateTicket(any(Long.class), any(UpdateTicketUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("Invalid update details"));

        mockMvc.perform(put("/api/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\":\"value\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTicketsByStatus_ValidStatusWithTickets() throws Exception {
        TicketStatus ticketStatus = TicketStatus.OPEN;
        TicketStatusDTO ticketStatusDTO = new TicketStatusDTO();
        List<TicketStatusDTO> tickets = Collections.singletonList(ticketStatusDTO);

        when(ticketService.getTicketsByStatus(any(TicketStatus.class))).thenReturn(tickets);

        mockMvc.perform(get("/api/tickets/status/OPEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetTicketsByStatus_ValidStatusNoTickets() throws Exception {
        when(ticketService.getTicketsByStatus(any(TicketStatus.class)))
                .thenThrow(new ResourceNotFoundException("No tickets found with status: CLOSED"));

        mockMvc.perform(get("/api/tickets/status/CLOSED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

//    @Test
//    void testGetTicketsByStatus_InvalidStatus() throws Exception {
//        when(ticketService.getTicketsByStatus(any(TicketStatus.class)))
//                .thenThrow(new ResourceNotFoundException("Invalid status"));
//
//        mockMvc.perform(get("/api/tickets/status/INVALID_STATUS")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Resource not found"));
//    }


    @Test
    void testGetAllTicketsByRaisedById_ValidUserIdWithTickets() throws Exception {
        Integer raisedById = 1;
        TicketStatusDTO ticketStatusDTO = new TicketStatusDTO();
        List<TicketStatusDTO> tickets = Collections.singletonList(ticketStatusDTO);

        when(ticketService.getAllTicketsByRaisedById(any(Integer.class))).thenReturn(tickets);

        mockMvc.perform(get("/api/tickets/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAllTicketsByRaisedById_ValidUserIdNoTickets() throws Exception {
        when(ticketService.getAllTicketsByRaisedById(any(Integer.class)))
                .thenThrow(new ResourceNotFoundException("No tickets found for user ID: 2"));

        mockMvc.perform(get("/api/tickets/user/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetAllTicketsByRaisedById_InvalidUserId() throws Exception {
        when(ticketService.getAllTicketsByRaisedById(any(Integer.class)))
                .thenThrow(new ResourceNotFoundException("Invalid user ID"));

        mockMvc.perform(get("/api/tickets/user/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }


    @Test
    void testTicketsByPriority_ValidPriorityWithTickets() throws Exception {
        PriorityLevel priorityLevel = PriorityLevel.HIGH;
        PriorityResponseDTO priorityResponseDTO = new PriorityResponseDTO();
        ApiResponse<List<PriorityResponseDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(priorityResponseDTO));
        apiResponse.setMessage("Successfully fetched tickets by priority");

        when(ticketService.getAllTicketsByPriority(any(PriorityLevel.class))).thenReturn(apiResponse);

        mockMvc.perform(get("/api/tickets/priority/HIGH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched tickets by priority"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllTicketsByPriority_ValidPriorityNoTickets() throws Exception {
        when(ticketService.getAllTicketsByPriority(any(PriorityLevel.class)))
                .thenThrow(new ResourceNotFoundException("No tickets found for priority: LOW"));

        mockMvc.perform(get("/api/tickets/priority/LOW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTicketsByCategory_ValidCategoryWithTickets() throws Exception {
        TicketCategory ticketCategory = TicketCategory.SOFTWARE;
        PriorityResponseDTO priorityResponseDTO = new PriorityResponseDTO();
        ApiResponse<List<PriorityResponseDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(priorityResponseDTO));
        apiResponse.setMessage("Successfully fetched tickets by category");

        when(ticketService.getTicketsByCategory(any(TicketCategory.class))).thenReturn(apiResponse);

        mockMvc.perform(get("/api/tickets/category/SOFTWARE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched tickets by category"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetTicketsByCategory_ValidCategoryNoTickets() throws Exception {
        when(ticketService.getTicketsByCategory(any(TicketCategory.class)))
                .thenThrow(new ResourceNotFoundException("No tickets found for category: TRAINING"));

        mockMvc.perform(get("/api/tickets/category/TRAINING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetCountOfTicketsByCategory_TicketsExist() throws Exception {
        ApiResponse<Map<TicketCategory, Long>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Map.of(TicketCategory.SOFTWARE, 2L));
        apiResponse.setMessage("Successfully fetched count of tickets by category");

        when(ticketService.getCountOfTicketsByCategory()).thenReturn(apiResponse);

        mockMvc.perform(get("/api/tickets/count/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched count of tickets by category"))
                .andExpect(jsonPath("$.data.SOFTWARE").value(2));
    }

    @Test
    void testGetCountOfTicketsByCategory_NoTickets() throws Exception {
        when(ticketService.getCountOfTicketsByCategory())
                .thenThrow(new ResourceNotFoundException("No tickets found in the system."));

        mockMvc.perform(get("/api/tickets/count/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetAllTickets_TicketsExist() throws Exception {
        PriorityResponseDTO priorityResponseDTO = new PriorityResponseDTO();
        ApiResponse<List<PriorityResponseDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(priorityResponseDTO));
        apiResponse.setMessage("Successfully fetched all tickets");

        when(ticketService.getAllTickets()).thenReturn(apiResponse);

        mockMvc.perform(get("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched all tickets"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllTickets_NoTickets() throws Exception {
        when(ticketService.getAllTickets())
                .thenThrow(new ResourceNotFoundException("No tickets found in the system."));

        mockMvc.perform(get("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

//    @Test
//    void testGetTicketsByStatus_Success() {
//        when(ticketService.getTicketsByStatus(any(TicketStatus.class))).thenReturn(ticketList);
//        ResponseEntity<?> response = ticketController.getTicketsByStatus(validTicketStatus);
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        verify(ticketService, times(1)).getTicketsByStatus(any(TicketStatus.class));
//    }

    @Test
    void testGetTicketsByStatus_NoResults() {
        when(ticketService.getTicketsByStatus(any(TicketStatus.class))).thenReturn(new ArrayList<>());
        Exception exception = assertThrows(NoTicketFoundException.class, () -> ticketController.getTicketsByStatus(validTicketStatus));
        assertEquals("No tickets found with status: " + validTicketStatus, exception.getMessage());
        verify(ticketService, times(1)).getTicketsByStatus(any(TicketStatus.class));
    }

    @Test
    void testGetTicketsByStatus_InvalidTicketStatus() {
        when(ticketService.getTicketsByStatus(any(TicketStatus.class)))
                .thenThrow(new RuntimeException("Invalid ticket status"));
        Exception exception = assertThrows(RuntimeException.class, () -> ticketController.getTicketsByStatus(invalidTicketStatus));
        assertEquals("Invalid ticket status", exception.getMessage());
        verify(ticketService, times(1)).getTicketsByStatus(any(TicketStatus.class));
    }

    @Test
    void testGetTicketsByStatus_UnauthorizedAccess() {
        when(ticketService.getTicketsByStatus(any(TicketStatus.class)))
                .thenThrow(new RuntimeException("Access denied"));
        Exception exception = assertThrows(RuntimeException.class, () -> ticketController.getTicketsByStatus(validTicketStatus));
        assertEquals("Access denied", exception.getMessage());
        verify(ticketService, times(1)).getTicketsByStatus(any(TicketStatus.class));
    }

    @Test
    void testGetAllTicketsByRaisedById_NoTicketsFound() {
        Integer userId = 999;
        when(ticketService.getAllTicketsByRaisedById(userId)).thenReturn(Collections.emptyList());

        NoTicketFoundException exception = assertThrows(NoTicketFoundException.class,
                () -> ticketController.getAllTicketsByRaisedById(userId));

        assertEquals("No tickets found for user ID: 999", exception.getMessage());
        verify(ticketService, times(1)).getAllTicketsByRaisedById(userId);
    }
}
