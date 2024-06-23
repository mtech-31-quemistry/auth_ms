package com.quemistry.auth_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

   @Bean
    public RedisConnectionFactory lettuceConnectionFactory(){
       return new LettuceConnectionFactory((new RedisStandaloneConfiguration()));
   }

   @Bean
    public RedisConnectionFactory jedisConnectionFactory(){
       return new JedisConnectionFactory(new RedisStandaloneConfiguration());
   }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
