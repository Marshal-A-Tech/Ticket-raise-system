package com.itil.controller;

import com.itil.dto.*;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.NoTicketFoundException;
import com.itil.service.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private TicketService ticketService;


    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<RaiseTicketResponseDTO>> raiseTicket(@PathVariable Long userId, @Valid @RequestBody RaiseTicketDTO raiseTicketDTO) {
        logger.info("User raising a ticket: " + userId);
        ApiResponse<RaiseTicketResponseDTO> response = ticketService.raiseTicket(userId, raiseTicketDTO);
        logger.info("Ticket raised successfully for user id: " + userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{ticketId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<UpdateTicketUserResponseDTO>>updateTicket(@PathVariable Long ticketId,
                                                                                @RequestBody UpdateTicketUserRequest updateTicketUserRequest) {
        logger.info("Updating ticket with ID: " + ticketId);
        ApiResponse<UpdateTicketUserResponseDTO> response = ticketService.updateTicket(ticketId, updateTicketUserRequest);
        logger.info("Ticket updated successfully." + ticketId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/status/{ticketStatus}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER', 'ROLE_TEAMMEMBER')")
    public ResponseEntity<?> getTicketsByStatus(@PathVariable TicketStatus ticketStatus) {
        logger.info("Fetching tickets with status: {}", ticketStatus);
        List<TicketStatusDTO> tickets = ticketService.getTicketsByStatus(ticketStatus);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found with status: {}", ticketStatus);
            throw new NoTicketFoundException("No tickets found with status: " + ticketStatus);
        }
        logger.info("Successfully retrieved {} tickets with status: {}", tickets.size(), ticketStatus);
        return ResponseEntity.ok(tickets);
    }


    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<List<PriorityResponseDTO>>> getAllTicketsByPriority(@PathVariable PriorityLevel priority) {
        logger.info("Fetching all tickets by priority: " + priority);
        ApiResponse<List<PriorityResponseDTO>> tickets = ticketService.getAllTicketsByPriority(priority);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{raisedById}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER', 'ROLE_USER')")
    public ResponseEntity<List<TicketStatusDTO>> getAllTicketsByRaisedById(@PathVariable Integer raisedById) {
        logger.info("Fetching tickets raised by user with ID: {}", raisedById);
        List<TicketStatusDTO> tickets = ticketService.getAllTicketsByRaisedById(raisedById);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for user ID: {}", raisedById);
            throw new NoTicketFoundException("No tickets found for user ID: " + raisedById);
        }
        logger.info("Successfully retrieved {} tickets for user ID: {}", tickets.size(), raisedById);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/category/{ticketCategory}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<List<PriorityResponseDTO>>> getTicketsByCategory(@PathVariable TicketCategory ticketCategory) {
        logger.info("Fetching tickets by category: " + ticketCategory);
        ApiResponse<List<PriorityResponseDTO>> tickets = ticketService.getTicketsByCategory(ticketCategory);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/count/category")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<Map<TicketCategory, Long>>> getCountOfTicketsByCategory() {
        logger.info("Fetching count of tickets by category");
        ApiResponse<Map<TicketCategory, Long>> categoryCount = ticketService.getCountOfTicketsByCategory();
        return ResponseEntity.ok(categoryCount);

    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<List<PriorityResponseDTO>>> getAllTickets() {
        logger.info("Successfully fetched all tickets");
        ApiResponse<List<PriorityResponseDTO>> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
}