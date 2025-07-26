package com.itil.repository;

import com.itil.entity.TeamMember;
import com.itil.enums.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTicketCategory(TicketCategory ticketCategory);

    //List<Ticket> findByAssignedToIdAndPriorityLevel(Long teamMemberId, PriorityLevel priorityLevel);
}
