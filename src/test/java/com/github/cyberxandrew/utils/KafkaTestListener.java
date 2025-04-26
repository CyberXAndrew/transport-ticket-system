package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.model.Ticket;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
//@Scope("prototype")
public class KafkaTestListener {

    private AtomicBoolean messageReceived = new AtomicBoolean(false);
    private Ticket receivedMessage;
//    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @KafkaListener(topics = "purchased-ticket", groupId = "test-consumers-one")
    public void listenTopic(Ticket ticket) throws InterruptedException {//fix
        System.out.println("======[MEthod listen start]:\n\n------");// TEST TEMPORARY STRING
//        countDownLatch.countDown();
        receivedMessage = ticket;
        messageReceived.set(true);
//        Thread.sleep(12350);
        System.out.println("Message received: " + ticket);

        System.out.println("======[message true?]:\n" + messageReceived + "\n------");// TEST TEMPORARY STRING
        System.out.println("======[ticket]:\n" + ticket + "\n------");// TEST TEMPORARY STRING
//        System.out.println("======[countdownlatch]:\n" + countDownLatch.getCount() + "\n------");// TEST TEMPORARY STRING
    }

    public void reset() {
        messageReceived.set(false);
        receivedMessage = null;
//        countDownLatch = new CountDownLatch(1);
    }

    public AtomicBoolean getMessageReceived() {
        return messageReceived;
    }

    public Ticket getReceivedMessage() {
        return receivedMessage;
    }

//    public CountDownLatch getCountDownLatch() {
//        return countDownLatch;
//    }
}
