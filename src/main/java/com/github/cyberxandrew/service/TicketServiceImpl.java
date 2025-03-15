package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.repository.TicketRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class TicketServiceImpl implements TicketService {

    @Autowired private TicketRepositoryImpl ticketRepository;

    @Override
    public Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + ticketId + " not found"));
    }

    @Override
    public List<Ticket> findTicketByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public List<TicketDTO> findAllAccessibleTickets(Pageable pageable, LocalDateTime dateTime, String departurePoint,
                                                    String destinationPoint, String carrierName) {
        return ticketRepository.findAll(pageable, dateTime, departurePoint, destinationPoint, carrierName);
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        if (ticket == null) throw new TicketSaveException("Error while saving or updating Ticket: Ticket is null");
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    public boolean isTicketAvailable(Long ticketId) {
        return ticketRepository.isTicketAvailable(ticketId);
    }
}
