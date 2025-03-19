package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface TicketMapper {

    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    TicketDTO ticketToTicketDTO(Ticket ticket);
    Ticket ticketDTOToTicket(TicketDTO ticketDTO);
    Ticket ticketCreateDTOToTicket(TicketCreateDTO createDTO);
    TicketDTO ticketCreateDTOToTicketDTO(TicketCreateDTO createDTO);
    Ticket ticketUpdateDTOToTicket(TicketUpdateDTO updateDTO);
    TicketDTO ticketUpdateDTOToTicketDTO(TicketUpdateDTO updateDTO);
    TicketUpdateDTO ticketDTOToUpdateDTO(TicketDTO ticketDTO);
}
