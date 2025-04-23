package com.github.cyberxandrew.service;

import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired private TicketMapper ticketMapper;
    @Autowired private TicketProduser ticketProduser;
    @Autowired private TicketCacheService ticketCacheService;
    @Autowired private TicketRepositoryImpl ticketRepository;
    private static Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public TicketDTO findTicketById(Long ticketId) {
        return ticketMapper.ticketToTicketDTO(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + ticketId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> findAllPurchasedTickets(Long userId) {
        List<Ticket> purchasedTicketsFromCache = ticketCacheService.getPurchasedTickets(userId);
        if (purchasedTicketsFromCache != null && !purchasedTicketsFromCache.isEmpty()) {
            return purchasedTicketsFromCache.stream()
                    .map(ticket -> ticketMapper.ticketToTicketDTO(ticket))
                    .toList();
        }
        List<Ticket> tickets = ticketRepository.findAllPurchasedTickets(userId);
        ticketCacheService.cachePurchasedTickets(userId, tickets);

        return tickets.stream().map(ticketMapper::ticketToTicketDTO).toList();
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
        if (createDTO == null) throw new TicketSaveException("Error while saving Ticket: Ticket to save is null");
        Ticket ticket = ticketMapper.ticketCreateDTOToTicket(createDTO);
        return ticketMapper.ticketToTicketDTO(ticketRepository.save(ticket));
    }

    @Override
    @Transactional
    public TicketDTO updateTicket(TicketUpdateDTO updateDTO, Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() ->
            new TicketUpdateException("Error while updating Ticket: Ticket by id is null"));

        ticketMapper.update(updateDTO, ticket);
        return ticketMapper.ticketToTicketDTO(ticketRepository.update(ticket));
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    @Transactional(readOnly = true)
    public void purchaseTicket(Long userId, Long ticketId) {
        ticketRepository.purchaseTicket(userId, ticketId);

        sendTicket(ticketId);

        List<Ticket> tickets = ticketRepository.findAllPurchasedTickets(userId);
        ticketCacheService.cachePurchasedTickets(userId, tickets);
    }

    private void sendTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() ->
                new TicketNotFoundException("Ticket with id " + ticketId + " not found"));
        ticketProduser.sendMessage(ticket);
    }
}
