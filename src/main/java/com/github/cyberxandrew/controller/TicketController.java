package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.dto.TicketWithRouteDataDTO;
import com.github.cyberxandrew.mapper.TicketMapper;
import com.github.cyberxandrew.model.Ticket;
import com.github.cyberxandrew.service.TicketServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/tickets")
public class TicketController {

    @Autowired TicketServiceImpl ticketService;
    /* TODO : Входные данные REST методов должны валидироваться.
         В случае ошибки валидации должна возвращаться HTTP ошибка 400,
          а в теле ответа – сообщение об ошибке (нужно придумать любой формат тела ответа,
           отличный от стандартного в Spring Web */

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketDTO show(@Valid @PathVariable Long id) {
        return ticketService.findTicketById(id);
    }
    @GetMapping(path = "")
    public ResponseEntity<List<TicketWithRouteDataDTO>> index(@Valid @RequestParam(required = false) Pageable pageable,
                                                                @RequestParam(required = false) LocalDateTime dateTime,
                                                                @RequestParam(required = false) String departurePoint,
                                                                @RequestParam(required = false) String destinationPoint,
                                                                @RequestParam(required = false) String carrierName) {
        List<TicketWithRouteDataDTO> allAccessibleTickets = ticketService.findAllAccessibleTickets(pageable, dateTime,
                departurePoint, destinationPoint, carrierName);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(allAccessibleTickets.size()))
                .body(allAccessibleTickets);
    }


    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDTO create(@Valid @RequestParam TicketCreateDTO ticketCreateDTO) {
        return ticketService.saveTicket(ticketCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketDTO update(@Valid @RequestParam TicketUpdateDTO updateDTO, @PathVariable Long id) {
        return ticketService.updateTicket(updateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}
