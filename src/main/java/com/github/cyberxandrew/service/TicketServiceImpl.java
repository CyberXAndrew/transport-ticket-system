package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.exception.ticket.TicketNotFoundException;
import com.github.cyberxandrew.exception.ticket.TicketSaveException;
import com.github.cyberxandrew.exception.ticket.TicketUpdateException;
import com.github.cyberxandrew.mapper.TicketMapper;
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
    public TicketDTO findTicketById(Long ticketId) {
        return TicketMapper.INSTANCE.ticketToTicketDTO(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + ticketId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> findTicketByUserId(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);
        return tickets.stream().map(TicketMapper.INSTANCE::ticketToTicketDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketWithRouteDataDTO> findAllAccessibleTickets(Pageable pageable, LocalDateTime dateTime, String departurePoint,
                                                                 String destinationPoint, String carrierName) {
        return ticketRepository.findAll(pageable, dateTime, departurePoint, destinationPoint, carrierName);
    }

    public List<TicketWithRouteDataDTO> findAllAccessibleTickets() { return ticketRepository.findAll(); }

    @Override
    @Transactional
    public TicketDTO saveTicket(TicketCreateDTO createDTO) {
        if (createDTO == null) {
            throw new TicketSaveException("Error while saving or updating Ticket: Ticket is null");
        }
        Ticket ticket = TicketMapper.INSTANCE.ticketCreateDTOToTicket(createDTO);
        return TicketMapper.INSTANCE.ticketToTicketDTO(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketDTO updateTicket(TicketUpdateDTO updateDTO, Long id) {
        if (id == null) {
            throw new TicketUpdateException("Error while saving or updating Ticket: Ticket is null");
        }
        Ticket ticket = ticketRepository.findById(id).get();
        return TicketMapper.INSTANCE.ticketToTicketDTO(ticketRepository.update(ticket));
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
