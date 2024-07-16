package com.quemistry.auth_ms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {


    private final String host;

    private final int port;

    private final Boolean useSSL;

    public RedisConfig(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.ssl.enabled}") Boolean useSSL) {
        this.host = host;
        this.port = port;
        this.useSSL = useSSL;
    }

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory(){
       LettuceClientConfiguration clientConfig;

       if(this.useSSL){
           clientConfig= LettuceClientConfiguration.builder().useSsl().build();
       }
       else {
           clientConfig= LettuceClientConfiguration.builder().build();
       }
       return new LettuceConnectionFactory((new RedisStandaloneConfiguration(this.host, this.port)),clientConfig);
   }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        return template;
    }
}
