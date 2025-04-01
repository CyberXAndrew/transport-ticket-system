package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.ticket.TicketCreateDTO;
import com.github.cyberxandrew.dto.ticket.TicketDTO;
import com.github.cyberxandrew.dto.ticket.TicketUpdateDTO;
import com.github.cyberxandrew.dto.ticket.TicketWithRouteDataDTO;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.service.TicketServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/tickets")
public class TicketController {
    @Autowired TicketServiceImpl ticketService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketDTO show(@PathVariable Long id) {
        return ticketService.findTicketById(id);
    }

    @GetMapping(path = "/purchased")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketDTO> findAllPurchasedTickets(@RequestParam Long userId) {
        return ticketService.findAllPurchasedTickets(userId);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<TicketWithRouteDataDTO>> index(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer size,
                                                              @RequestParam(required = false) LocalDateTime dateTime,
                                                              @RequestParam(required = false) String departurePoint,
                                                              @RequestParam(required = false) String destinationPoint,
                                                              @RequestParam(required = false) String carrierName) {
        Pageable pageable = null;
        if (page != null && size != null) pageable = PageRequest.of(page, size);
        List<TicketWithRouteDataDTO> allAccessibleTickets = ticketService.findAllAccessibleTickets(pageable, dateTime,
                departurePoint, destinationPoint, carrierName);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(allAccessibleTickets.size()))
                .body(allAccessibleTickets);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDTO create(@Valid @RequestBody TicketCreateDTO ticketCreateDTO) {
        return ticketService.saveTicket(ticketCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketDTO update(@Valid @RequestBody TicketUpdateDTO updateDTO, @PathVariable Long id) {
        return ticketService.updateTicket(updateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    @PostMapping(path = "/{id}/purchase")
    @ResponseStatus(HttpStatus.OK)
    public void purchaseTicket(@PathVariable Long id, @RequestParam Long userId) {
        ticketService.purchaseTicket(userId, id);
    }
}
