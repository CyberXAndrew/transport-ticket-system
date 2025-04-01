package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketService {
    TicketDTO findTicketById(Long ticketId);
    List<TicketDTO> findAllPurchasedTickets(Long userId);
    List<TicketWithRouteDataDTO> findAllAccessibleTickets(Pageable pageable,
                                                          LocalDateTime dateTime,
                                                          String departurePoint,
                                                          String destinationPoint,
                                                          String carrierName);
    TicketDTO saveTicket(TicketCreateDTO createDTO);
    TicketDTO updateTicket(TicketUpdateDTO updateDTO, Long id);
    void deleteTicket(Long ticketId);
    boolean isTicketAvailable(Long ticketId);
}
