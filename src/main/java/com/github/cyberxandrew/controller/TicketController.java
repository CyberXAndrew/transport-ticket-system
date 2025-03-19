package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.*;
import com.github.cyberxandrew.service.TicketServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
