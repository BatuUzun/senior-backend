package com.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean(name = "redisTemplateLong")
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        // Use GenericToStringSerializer for Long values
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }
}
