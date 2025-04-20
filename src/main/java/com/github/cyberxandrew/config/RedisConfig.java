package com.github.cyberxandrew.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberxandrew.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Autowired private ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, Ticket> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Ticket> redisTemplate = new RedisTemplate<>();

        Jackson2JsonRedisSerializer<Ticket> ticketJackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Ticket.class);

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(ticketJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(ticketJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
