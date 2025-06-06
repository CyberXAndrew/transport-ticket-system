package com.github.cyberxandrew.service;

import com.github.cyberxandrew.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class TicketCacheService {

    @Autowired private RedisTemplate<String, Ticket> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TicketCacheService.class);

    public void cachePurchasedTickets(Long userId, List<Ticket> tickets) {
        String key = "purchasedTickets:" + userId;
        if (tickets != null && !tickets.isEmpty()) {
            try {
                redisTemplate.opsForList().trim(key, 1, 0);
                redisTemplate.opsForList().leftPushAll(key, tickets);
                redisTemplate.expire(key, Duration.ofHours(1));
            } catch (DataAccessException ex) {
                logger.warn("Error while caching purchased tickets because tickets list is null", ex);
            }
        }
    }

    public List<Ticket> getPurchasedTickets(Long userId) {
        String key = "purchasedTickets:" + userId;
        try {
            return Objects.requireNonNull(redisTemplate.opsForList().range(key, 0, -1));
        } catch (DataAccessException | NullPointerException ex) {
            logger.warn("Error getting purchased tickets for user with id: {}", userId, ex);
            return Collections.emptyList();
        }
    }

    public void evictPurchasedTickets(Long userId, int startIndex, int endIndex) {
        String key = "purchasedTickets:" + userId;
        try {
            redisTemplate.opsForList().trim(key, startIndex, endIndex);
        } catch (DataAccessException ex) {
            logger.warn("Error while deleting cached data from key: {}", key, ex);
        }
    }
}
