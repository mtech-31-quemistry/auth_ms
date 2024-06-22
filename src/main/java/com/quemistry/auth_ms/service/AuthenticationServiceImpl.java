package com.quemistry.auth_ms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${quemistry.cognito.url}")
    private String QUEMISTRY_COGNITO_URL;

    @Value("${quemistry.session.timeout}")
    private int SESSION_TIMEOUT;

    private final RestTemplate restTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    public AuthenticationServiceImpl(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserProfile getAccessToken(TokenRequest request) {
        final String tokenUri = "https://quemistry.auth.ap-southeast-1.amazoncognito.com/oauth2/token";
        UserProfile user = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type","authorization_code");
        formData.add("redirect_uri",request.getRedirectUrl());
        formData.add("client_id",request.getClientId());
        formData.add("code",request.getAuthCode());
        formData.add("code_verifier",request.getCodeVerifier());
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(tokenUri, formEntity, TokenResponse.class );

        if(response.getStatusCode() == HttpStatus.OK){
            var idToken = response.getBody().getIdToken();
            String[] chunks = idToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> map = mapper.readValue(payload, new TypeReference<>() {});
                //creates session id as key and store user profile and access tokens info in redis
                user = new UserProfile();
                user.setSessionId(UUID.randomUUID().toString());
                user.setEmail((String)map.get("email"));

                redisTemplate.opsForValue().set(user.getSessionId()+"_profile", user, Duration.ofSeconds(SESSION_TIMEOUT));
                redisTemplate.opsForValue().set(user.getSessionId()+"_tokens", response.getBody(), Duration.ofSeconds(SESSION_TIMEOUT));
            }
            catch (Exception ex) {
                log.error("Error reading and saving session in getAccessToken",ex);
            }

            return user;
        }
        else{
            //log error
            return null;
        }
        //store token in redis cache
    }

    @Override
    public void signOut(String sessionId, String clientId) {
        final String tokenUri = "https://quemistry.auth.ap-southeast-1.amazoncognito.com/oauth2/revoke";
        var tokens = ((TokenResponse) redisTemplate.opsForValue().getAndDelete(sessionId + "_tokens"));
        if(tokens != null) {
            redisTemplate.opsForValue().getAndDelete(sessionId + "_profile");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", tokens.getRefreshToken());
            formData.add("client_id", clientId);
            HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formData, headers);

            restTemplate.postForEntity(tokenUri, formEntity, String.class );
        }
    }

}
