package com.itil.serviceimpl;

import com.itil.dto.*;
import com.itil.entity.Gatekeeper;
import com.itil.entity.Ticket;
import com.itil.entity.User;
import com.itil.entity.UserInfo;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TicketRepository;
import com.itil.repository.UserRepository;
import com.itil.service.TicketService;
import com.itil.util.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GatekeeperRepository gatekeeperRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<RaiseTicketResponseDTO> raiseTicket(Long userId, RaiseTicketDTO raiseTicketDTO) {
        logger.info("Attempting to raise a new ticket for User Id: " + userId);
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            logger.error("User not found with Id: " + userId);
            throw new ResourceNotFoundException("User not found");
        }
        Optional<Gatekeeper> gatekeeper = gatekeeperRepository.findFirstByOrderByIdAsc();
        if(gatekeeper.isEmpty()) {
            logger.error("No gatekeeper found in this system");
            throw new ResourceNotFoundException("No Gatekeeper found.");
        }
        Ticket ticket = new Ticket();
        ticket.setTicketCategory(raiseTicketDTO.getTicketCategory());
        ticket.setTitle(raiseTicketDTO.getTitle());
        ticket.setDescription(raiseTicketDTO.getDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setRaisedBy(existUser.get());
        ticket.setPriorityLevel(raiseTicketDTO.getPriorityLevel());
        ticket.setTicketStatus(TicketStatus.OPEN);
        ticket.setGatekeeper(gatekeeper.get());
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket raised successfully");
        RaiseTicketResponseDTO raiseTicketResponseDTO = modelMapper.map(savedTicket, RaiseTicketResponseDTO.class);
        return ResponseUtil.success(raiseTicketResponseDTO, "Ticket Raised Successfully");
    }

    @Override
    public ApiResponse<UpdateTicketUserResponseDTO> updateTicket(Long ticketId,  UpdateTicketUserRequest updateTicketUserRequest) {
        logger.info("Updating the ticket: " + ticketId);
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        ticket.setTicketStatus(updateTicketUserRequest.getTicketStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setResolvedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        UpdateTicketUserResponseDTO response = modelMapper.map(ticket, UpdateTicketUserResponseDTO.class);
        logger.info("Ticket ID: " + ticketId + " updated successfully");
        return ResponseUtil.success(response, "Ticket updated successfully.");
    }

    @Override
    public List<TicketStatusDTO> getTicketsByStatus(TicketStatus ticketStatus) {
        List<Ticket> tickets = ticketRepository.findByStatus(ticketStatus);
        return tickets.stream()
                .map(this::mapToStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<List<PriorityResponseDTO>> getAllTicketsByPriority(PriorityLevel priority) {
        logger.info("Fetching all tickets by priority: " + priority);
        List<Ticket> tickets = ticketRepository.findByPriority(priority);
        if(tickets.isEmpty()) {
            logger.warn("No tickets found for priority: " + priority);
            throw new ResourceNotFoundException("No tickets found for priority: " + priority);
        }
        List<PriorityResponseDTO> response = tickets.stream().map(this::mapToPriorityDto)
                .collect(Collectors.toList());
        return ResponseUtil.success(response, "successfully fetched tickets by priority");
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
    public ApiResponse<List<PriorityResponseDTO>> getTicketsByCategory(TicketCategory ticketCategory) {
        logger.info("Fetching tickets by category: " + ticketCategory);
        List<Ticket> tickets = ticketRepository.findByTicketCategory(ticketCategory);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for category: " + ticketCategory);
            throw new ResourceNotFoundException("No tickets found for category: " + ticketCategory);
        }
        List<PriorityResponseDTO> response = tickets.stream().map(this::mapToPriorityDto).collect(Collectors.toList());
        return ResponseUtil.success(response, "Successfully fetched tickets by category");
    }

    @Override
    public ApiResponse<Map<TicketCategory, Long>> getCountOfTicketsByCategory() {
        logger.info("Fetching count of tickets by category");
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()) {
            logger.warn("No tickets found in the system.");
            throw new ResourceNotFoundException("No tickets found in the system.");
        }
        Map<TicketCategory, Long> categoryCount = tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getTicketCategory, Collectors.counting()));
        return ResponseUtil.success(categoryCount, "Successfully fetched count of tickets by category");
    }

    @Override
    public ApiResponse<List<PriorityResponseDTO>> getAllTickets() {
        logger.info("Fetching all tickets");
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()) {
            logger.warn("No tickets found in the system.");
            throw new ResourceNotFoundException("No tickets found in the system.");
        }
        List<PriorityResponseDTO> response = tickets.stream().map(this::mapToPriorityDto).collect(Collectors.toList());
        return ResponseUtil.success(response, "Successfully fetched all tickets");
    }

    private PriorityResponseDTO mapToPriorityDto(Ticket ticket) {
        PriorityResponseDTO priorityResponseDTO = new PriorityResponseDTO();
        priorityResponseDTO.setId(ticket.getId());
        priorityResponseDTO.setTitle(ticket.getTitle());
        priorityResponseDTO.setDescription(ticket.getDescription());
        priorityResponseDTO.setTicketStatus(ticket.getTicketStatus());
        priorityResponseDTO.setTicketCategory(ticket.getTicketCategory());
        return priorityResponseDTO;
    }

    @Override
    public List<TicketStatusDTO> getAllTicketsByRaisedById(Integer raisedById) {
        logger.info("Getting tickets based on userId: " + raisedById);
        List<Ticket> tickets = ticketRepository.findByRaisedById(raisedById);
        if (tickets.isEmpty()) {
            logger.warn("No tickets found for userId: " + raisedById);
        }
        logger.info("Found [{}] tickets for userId [{}]", tickets.size(), raisedById);
        return tickets.stream().map(this::mapToStatusDto).collect(Collectors.toList());
    }

    private TicketStatusDTO mapToStatusDto(Ticket ticket) {
        TicketStatusDTO ticketDTO = new TicketStatusDTO();
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setTicketStatus(ticket.getTicketStatus());
        ticketDTO.setTicketCategory(ticket.getTicketCategory());
        ticketDTO.setPriorityLevel(ticket.getPriorityLevel());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        ticketDTO.setUpdatedAt(ticket.getUpdatedAt());
        return ticketDTO;
    }
}

