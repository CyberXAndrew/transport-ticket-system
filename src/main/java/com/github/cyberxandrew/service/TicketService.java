package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.model.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TicketService {
    TicketDTO saveTicket(TicketCreateDTO createDTO);
    TicketDTO updateTicket(TicketUpdateDTO updateDTO, Long id);
    TicketDTO findTicketById(Long ticketId);
    List<TicketDTO> findTicketByUserId(Long userId);
    List<TicketWithRouteDataDTO> findAllAccessibleTickets(Pageable pageable, LocalDateTime dateTime,
                                                          String departurePoint, String destinationPoint, String carrierName);
    void deleteTicket(Long ticketId);
    boolean isTicketAvailable(Long ticketId);
}
