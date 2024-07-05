package com.quemistry.auth_ms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory(){
       var clientConfig = LettuceClientConfiguration.builder().useSsl().build();
       return new LettuceConnectionFactory((new RedisStandaloneConfiguration("clustercfg.quesmistry-redis.vrbbrp.apse1.cache.amazonaws.com", 6379)),clientConfig);
   }

   //@Bean
   // public RedisConnectionFactory jedisConnectionFactory(){
  //     var clientConfig = JedisClientConfiguration.builder().useSsl().build();
  //     return new JedisConnectionFactory(new RedisStandaloneConfiguration(),clientConfig);
  // }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        return template;
    }
}
