package com.itil.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.itil.dto.ApiResponse;
import com.itil.dto.RaiseTicketResponseDTO;
import com.itil.dto.UpdateTicketRequest;
import com.itil.dto.UpdateTicketResponseDTO;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketStatus;
import com.itil.exception.GlobalExceptionHandler;
import com.itil.exception.ResourceNotFoundException;
import com.itil.service.TeamMemberService;

@ExtendWith(MockitoExtension.class)
public class TeamMemberControllerTest {

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private TeamMemberController teamMemberController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Logger logger;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamMemberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAssignedTickets_ValidTeamMemberId() throws Exception {
        ApiResponse<List<RaiseTicketResponseDTO>> response = new ApiResponse<>();
        response.setData(Collections.emptyList());
        response.setMessage("Tickets retrieved successfully");


        when(teamMemberService.getAssignedTicketsById(any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/teamMembers/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"));
    }

    @Test
    void testGetAssignedTickets_InvalidTeamMemberId() throws Exception {
        when(teamMemberService.getAssignedTicketsById(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Team Member not found"));
        mockMvc.perform(get("/api/teamMembers/99/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_TEAMMEMBER")
    void updateTicketStatus_success() {
        Long ticketId = 1L;
        Long teamMemberId = 2L;
        UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest();
        updateTicketRequest.setResolutionDetails("Completed");
        updateTicketRequest.setTicketStatus(TicketStatus.COMPLETED);

        UpdateTicketResponseDTO expectedResponseDTO = new UpdateTicketResponseDTO();
        expectedResponseDTO.setTicketId(ticketId);
        expectedResponseDTO.setTicketStatus(TicketStatus.COMPLETED);
        expectedResponseDTO.setTicketId(ticketId);
        expectedResponseDTO.setUpdatedAt(LocalDateTime.now());
        expectedResponseDTO.setPriorityLevel(PriorityLevel.HIGH);
        expectedResponseDTO.setResolvedAt(LocalDateTime.now());
        expectedResponseDTO.setDescription("Not working");
        expectedResponseDTO.setTitle("Teams");
        expectedResponseDTO.setResolutionDetails("Not working");

        ApiResponse<UpdateTicketResponseDTO> expectedApiResponse = new ApiResponse<>();
        expectedApiResponse.setSuccess(true);
        expectedApiResponse.setData(expectedResponseDTO);
        expectedApiResponse.setMessage("Ticket updated successfully");

        when(teamMemberService.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest)).thenReturn(expectedApiResponse);

        ResponseEntity<ApiResponse<UpdateTicketResponseDTO>> responseEntity = teamMemberController.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedApiResponse, responseEntity.getBody());
    }

    @Test
    @WithMockUser(authorities = "ROLE_TEAMMEMBER")
    void updateTicketStatus_resourceNotFound_ticket() {
        Long ticketId = 1L;
        Long teamMemberId = 2L;
        UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest();

        when(teamMemberService.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest))
                .thenThrow(new ResourceNotFoundException("Ticket not found"));

        assertThrows(ResourceNotFoundException.class, () -> teamMemberController.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest));
    }

    @Test
    @WithMockUser(authorities = "ROLE_TEAMMEMBER")
    void updateTicketStatus_resourceNotFound_unassigned() {
        Long ticketId = 1L;
        Long teamMemberId = 2L;
        UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest();

        when(teamMemberService.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest))
                .thenThrow(new ResourceNotFoundException("You are not assigned to this ticket."));

        assertThrows(ResourceNotFoundException.class, () -> teamMemberController.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest));
    }

    @Test
    @WithMockUser(authorities = "ROLE_TEAMMEMBER")
    void updateTicketStatus_serviceThrowsGenericException() {
        Long ticketId = 1L;
        Long teamMemberId = 2L;
        UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest();

        when(teamMemberService.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest))
                .thenThrow(new RuntimeException("Generic service error"));

        assertThrows(RuntimeException.class, () -> teamMemberController.updateTicketStatus(ticketId, teamMemberId, updateTicketRequest));
    }

    @Test
    void testGetAssignedTicketsByPriority_ValidTeamMemberIdAndPriorityLevel() throws Exception {
        ApiResponse<List<RaiseTicketResponseDTO>> response = new ApiResponse<>();
        response.setData(Collections.emptyList());
        response.setMessage("Tickets retrieved successfully");


        when(teamMemberService.getAssignedTicketsByPriority(any(Long.class), any(PriorityLevel.class))).thenReturn(response);

        mockMvc.perform(get("/api/teamMembers/1/tickets/priority/HIGH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"));
    }

}