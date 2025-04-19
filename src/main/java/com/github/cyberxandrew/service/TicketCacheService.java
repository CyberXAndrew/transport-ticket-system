package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class TicketCacheService {

    @Autowired private RedisTemplate redisTemplate;

    /*
    task:
        1) кеширование купленных билетов для каждого пользователя
        2) реализовать проверки
    */

    public void cachePurchasedTickets(Long userId, List<Ticket> tickets) {
        String key = "purchasedTickets:" + userId;
        redisTemplate.opsForValue().set(key, tickets, Duration.ofHours(1));
    }

    public List<Ticket> getPurchasedTickets(Long userId) {
        String key = "purchasedTickets:" + userId;
         return (List<Ticket>) redisTemplate.opsForValue().get(key);
    }

    public void evictPurchasedTickets(Long userId) { //?
        String key = "purchasedTickets:" + userId;
        redisTemplate.delete(key);
    }
}
