package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.model.Ticket;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findById(Long ticketId);
    List<TicketDTO> findAll();
    List<TicketDTO> findAll(Pageable pageable,
                            LocalDateTime dateTime,
                            String departurePoint,
                            String destinationPoint,
                            String carrierName);

    List<Ticket> findByUserId(Long userId);
    Ticket save(Ticket ticket);
    void deleteById(Long ticketId);
    boolean isTicketAvailable(Long ticketId);
}
