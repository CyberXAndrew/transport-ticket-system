package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.model.Ticket;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(uses = { JsonNullableMapper.class },
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
componentModel = MappingConstants.ComponentModel.SPRING,
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

//    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class); //Fix: DELETE THIS

    TicketDTO ticketToTicketDTO(Ticket ticket);
    Ticket ticketCreateDTOToTicket(TicketCreateDTO createDTO);
    TicketDTO ticketCreateDTOToTicketDTO(TicketCreateDTO createDTO);
    TicketDTO ticketUpdateDTOToTicketDTO(TicketUpdateDTO updateDTO);
    TicketUpdateDTO ticketDTOToUpdateDTO(TicketDTO ticketDTO);
    @Mapping(target = "id", ignore = true)
    void update(TicketUpdateDTO updateDTO, @MappingTarget Ticket ticket);
}
