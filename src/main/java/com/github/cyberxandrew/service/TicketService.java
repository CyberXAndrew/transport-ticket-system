package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.model.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TicketService {
    Ticket saveTicket(Ticket ticket);
    Ticket findTicketById(Long ticketId);
    List<Ticket> findTicketByUserId(Long userId);
    List<TicketDTO> findAllAccessibleTickets(Pageable pageable, LocalDateTime dateTime,
                    String departurePoint, String destinationPoint, String carrierName);
    void deleteTicket(Long ticketId);
    boolean isTicketAvailable(Long ticketId);
}
