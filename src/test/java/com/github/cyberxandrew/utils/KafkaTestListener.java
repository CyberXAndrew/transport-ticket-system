package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.model.Ticket;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Component
public class KafkaTestListener {

    private AtomicBoolean messageReceived = new AtomicBoolean(false);
    private Ticket receivedMessage;
//    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @KafkaListener(topics = "purchased-ticket", groupId = "test-consumers-one")
    public void listenTopic(Ticket ticket) {
        System.out.println("KafkaTestListener в классе слушателя: " + System.identityHashCode(this));
//        countDownLatch.countDown();
        receivedMessage = ticket;
        messageReceived.set(true);
        System.out.println("Message received: " + ticket);
    }

    public void reset() {
        messageReceived.set(false);
        receivedMessage = null;
//        countDownLatch = new CountDownLatch(1);
    }
}
