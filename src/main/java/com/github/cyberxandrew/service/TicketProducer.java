package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketProducer {

    @Autowired private KafkaTemplate<String, Ticket> kafkaTemplate;
    private static Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class); // Fix delete

    public void sendMessage(Ticket ticket) {
        String topic = "purchased-ticket";
        logger.info("Отправка сообщения в Kafka: topic={}, ticket={}", topic, ticket); // Логирование
        kafkaTemplate.send("purchased-ticket", ticket);
    }

    public void sendMessageToReturn(Ticket ticket) {
        kafkaTemplate.send("returned-ticket", ticket);
    }
}
