package com.itil.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.itil.dto.ApiResponse;
import com.itil.dto.TeamMemberDTO;
import com.itil.dto.TeamMemberResponseDTO;
import com.itil.dto.TicketDTO;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.GlobalExceptionHandler;
import com.itil.exception.ResourceNotFoundException;
import com.itil.service.GatekeeperService;

@ExtendWith(MockitoExtension.class)
public class GatekeeperControllerTest {

    @Mock
    private GatekeeperService gatekeeperService;

    @InjectMocks
    private GatekeeperController gatekeeperController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gatekeeperController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test

    void testAssignTicket_ValidAssignment() throws Exception {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketStatus(TicketStatus.ASSIGNED);
        ApiResponse<TicketDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(ticketDTO);
        apiResponse.setMessage("Successfully Assigned ticket to team member");
        when(gatekeeperService.assignTicket(any(Long.class), any(Long.class))).thenReturn(apiResponse);
        mockMvc.perform(post("/api/gatekeeper/tickets/1/assignees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully Assigned ticket to team member"))
                .andExpect(jsonPath("$.data.ticketStatus").value("ASSIGNED"));
    }

    @Test
    void testAssignTicket_TicketNotFound() throws Exception {
        when(gatekeeperService.assignTicket(any(Long.class), any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Ticket not found with ID: 1"));
        mockMvc.perform(post("/api/gatekeeper/tickets/1/assignees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTicketsByTeamMemberId_ValidTeamMember() throws Exception {
        TicketDTO ticketDTO = new TicketDTO();
        ApiResponse<List<TicketDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(ticketDTO));
        apiResponse.setMessage("Successfully fetched the tickets assigned to the Team Member");
        when(gatekeeperService.getTicketsByTeamMemberId(any(Long.class))).thenReturn(apiResponse);
        mockMvc.perform(get("/api/gatekeeper/teamMembers/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched the tickets assigned to the Team Member"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetTicketsByTeamMemberId_TeamMemberNotFound() throws Exception {
        when(gatekeeperService.getTicketsByTeamMemberId(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Team Member not found with ID: 1"));
        mockMvc.perform(get("/api/gatekeeper/teamMembers/1/tickets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTeamMemberById_ValidTeamMember() throws Exception {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        teamMemberDTO.setName("Test Member");
        ApiResponse<TeamMemberDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(teamMemberDTO);
        apiResponse.setMessage("Successfully retrieved the details of Team Member ID: 1");
        when(gatekeeperService.getTeamMemberById(any(Long.class))).thenReturn(apiResponse);
        mockMvc.perform(get("/api/gatekeeper/teamMembers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully retrieved the details of Team Member ID: 1"))
                .andExpect(jsonPath("$.data.name").value("Test Member"));

    }

    @Test
    void testGetTeamMemberById_TeamMemberNotFound() throws Exception {
        when(gatekeeperService.getTeamMemberById(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Team Member not found with ID: 1"));
        mockMvc.perform(get("/api/gatekeeper/teamMembers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTeamMembersByTicketCategory_ValidCategory() throws Exception {
        TeamMemberResponseDTO teamMemberResponseDTO = new TeamMemberResponseDTO();
        ApiResponse<List<TeamMemberResponseDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(teamMemberResponseDTO));
        apiResponse.setMessage("Successfully fetched team members for ticket category");
        when(gatekeeperService.getTeamMembersByTicketCategory(any(TicketCategory.class))).thenReturn(apiResponse);
        mockMvc.perform(get("/api/gatekeeper/teamMembers/category/SOFTWARE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully fetched team members for ticket category"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testAssignGatekeeper_ValidTeamMemberIdAndGatekeeperId() throws Exception {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData("Gatekeeper assigned successfully");
        response.setMessage("Gatekeeper assigned successfully");


        when(gatekeeperService.assignGateKeeper(any(Long.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(put("/api/gatekeeper/1/gatekeeper/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Gatekeeper assigned successfully"));
    }

    @Test
    void testAssignGatekeeper_InvalidGatekeeperId() throws Exception {
        when(gatekeeperService.assignGateKeeper(any(Long.class), any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Gatekeeper not found"));

        mockMvc.perform(put("/api/gatekeeper/1/gatekeeper/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }
}

