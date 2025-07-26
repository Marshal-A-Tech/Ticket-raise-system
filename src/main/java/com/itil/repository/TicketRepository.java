package com.itil.repository;

import com.itil.entity.Ticket;
import com.itil.enums.PriorityLevel;
import com.itil.enums.TicketCategory;
import com.itil.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(PriorityLevel priority);

    List<Ticket> findByAssignedToId(Long teamMemberId);

    List<Ticket> findByTicketCategory(TicketCategory ticketCategory);

    List<Ticket> findByAssignedToIdAndPriority(Long teamMemberId, PriorityLevel priorityLevel);

    boolean existsByTicketCategory(TicketCategory ticketCategory);

    List<Ticket> findByRaisedById(Integer raisedById);

}
