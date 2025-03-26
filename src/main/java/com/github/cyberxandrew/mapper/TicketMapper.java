package com.github.cyberxandrew.mapper;

import com.github.cyberxandrew.dto.TicketCreateDTO;
import com.github.cyberxandrew.dto.TicketDTO;
import com.github.cyberxandrew.dto.TicketUpdateDTO;
import com.github.cyberxandrew.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(uses = { JsonNullableMapper.class },
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
componentModel = MappingConstants.ComponentModel.SPRING,
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {
    TicketDTO ticketToTicketDTO(Ticket ticket);
    Ticket ticketCreateDTOToTicket(TicketCreateDTO createDTO);
    TicketDTO ticketCreateDTOToTicketDTO(TicketCreateDTO createDTO);
    TicketDTO ticketUpdateDTOToTicketDTO(TicketUpdateDTO updateDTO);
    TicketUpdateDTO ticketDTOToUpdateDTO(TicketDTO ticketDTO);
    @Mapping(target = "id", ignore = true)
    void update(TicketUpdateDTO updateDTO, @MappingTarget Ticket ticket);
}
