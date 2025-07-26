package com.itil.service;

import com.itil.dto.ApiResponse;
import com.itil.dto.TeamMemberDTO;
import com.itil.dto.TeamMemberResponseDTO;
import com.itil.dto.TicketDTO;
import com.itil.entity.TeamMember;
import com.itil.enums.TicketCategory;

import java.util.List;

public interface GatekeeperService {

    ApiResponse<TicketDTO> assignTicket(Long ticketId, Long teamMemberId);

    ApiResponse<List<TicketDTO>> getTicketsByTeamMemberId(Long teamMemberId);

    ApiResponse<TeamMemberDTO> getTeamMemberById(Long teamMemberId);

    ApiResponse<List<TeamMemberResponseDTO>>  getTeamMembersByTicketCategory(TicketCategory ticketCategory);

    ApiResponse<String> assignGateKeeper(Long teamMemberId, Long gatekeeperId);


}
