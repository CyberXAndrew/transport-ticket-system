package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.model.Ticket;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findById(Long ticketId);
    List<Ticket> findByUserId(Long userId);
    List<TicketWithRouteDataDTO> findAll(Pageable pageable,
                                         LocalDateTime dateTime,
                                         String departurePoint,
                                         String destinationPoint,
                                         String carrierName);
    Ticket save(Ticket ticket);
    Ticket update(Ticket ticket);
    void deleteById(Long ticketId);
    boolean isTicketAvailable(Long ticketId);
}
