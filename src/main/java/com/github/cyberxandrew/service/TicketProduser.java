package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketProduser {

    @Autowired private KafkaTemplate<String, Ticket> kafkaTemplate;

    public void sendMessage(Ticket ticket) {
        kafkaTemplate.send("purchased-ticket", ticket);
    }

}
