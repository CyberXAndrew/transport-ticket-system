package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.repository.TicketRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired private TicketRepositoryImpl ticketRepository;
    private static Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + ticketId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> findAllAccessibleTickets(Pageable pageable, LocalDateTime dateTime, String departurePoint,
                                                    String destinationPoint, String carrierName) {
        return ticketRepository.findAll(pageable, dateTime, departurePoint, destinationPoint, carrierName);
    }

    public List<TicketDTO> findAllAccessibleTickets() { return ticketRepository.findAll(); }

    @Override
    @Transactional
    public Ticket saveTicket(Ticket ticket) {
        if (ticket == null) {
            throw new TicketSaveException("Error while saving or updating Ticket: Ticket is null");
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTicketAvailable(Long ticketId) {
        return ticketRepository.isTicketAvailable(ticketId);
    }
}
