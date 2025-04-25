package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.model.Ticket;
//import lombok.Data;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class KafkaTestListener {

    private AtomicBoolean messageReceived = new AtomicBoolean(false);
    private Ticket receivedMessage;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @KafkaListener(topics = "purchased-ticket", groupId = "ticket-consumers")
    public void listenTopic(Ticket ticket) {
        messageReceived.set(true);
        receivedMessage = ticket;
        countDownLatch.countDown();
        System.out.println("Message received: " + ticket);
    }

    public void reset() {
        messageReceived.set(false);
        receivedMessage = null;
        countDownLatch = new CountDownLatch(1);
    }

    public AtomicBoolean getMessageReceived() {
        return messageReceived;
    }

    public Ticket getReceivedMessage() {
        return receivedMessage;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
