package com.quemistry.auth_ms.service;

import com.quemistry.auth_ms.Util.JwtHelper;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import com.quemistry.auth_ms.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    //@Value("${quemistry.cognito.url}")
    //private String QUEMISTRY_COGNITO_URL;

    @Value("${quemistry.session.timeout}")
    private int SESSION_TIMEOUT;

    private final RestTemplate restTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RoleRepository roleRepository;

    public AuthenticationServiceImpl(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate
    , RoleRepository roleRepository) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.roleRepository = roleRepository;
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
            if(idToken == null)
                return null;

            try {
                JwtHelper jwtIdToken = new JwtHelper(idToken);
                if(!jwtIdToken.getValid()){
                    log.error("Invalid Id token");
                    return null;
                }
                //creates session id as key and store user profile and access tokens info in redis
                user = new UserProfile();
                user.setSessionId(UUID.randomUUID().toString());
                user.setEmail(jwtIdToken.getEmail());
                user.setRoles(jwtIdToken.getUserGroup());

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

    @Override
    public Boolean checkAccess(String roleName, String path, String method) {
        //get role
        var role = roleRepository.findByName(roleName);
        if(role.isPresent()){
            var grantedWith = role.get().getGrantedWith();
            if(grantedWith.stream().anyMatch(granted -> granted.getPath().compareToIgnoreCase(path) == 0
                                            && granted.getMethod().compareToIgnoreCase(method) ==0))
                return true;
        }
        return false;
    }

    @Override
    public String checkUserSessionAccess(String sessionId, String path, String method) {
        //get user profile role
        log.info("checkUserSessionAccess invoked");
        String userId = "";
        var profile = ((UserProfile) redisTemplate.opsForValue().get(sessionId + "_profile"));
        var tokens = ((TokenResponse) redisTemplate.opsForValue().get(sessionId + "_tokens"));
        if(tokens == null || profile == null)
        {
            log.info("checkUserSessionAccess: session not found");
            return "";
        }else{
            log.info("checkUserSessionAccess: found. With roles:"+ String.join(";",profile.getRoles()));
            //check if user token has expired. If yes to refresh, asynchronously.
            JwtHelper jwtAccessToken = new JwtHelper(tokens.getAccessToken());
            userId = jwtAccessToken.getUserId();
        }
        //get role
        var roles = roleRepository.findByNames(profile.getRoles());
        if(roles.size() == 0){
            log.info("checkUserSessionAccess: roles does missing in data store.");
        }
        else{
            StringBuilder rolesFound = new StringBuilder();
            roles.forEach(role -> rolesFound.append(role.getName()+";"));
            log.info("checkUserSessionAccess: "+rolesFound);
        }
         if(roles.stream().anyMatch(role ->
            role.getGrantedWith().stream().anyMatch(granted -> granted.getPath().compareToIgnoreCase(path) == 0
                    && granted.getMethod().compareToIgnoreCase(method) == 0))){
            return userId;
        }
        return "";
    }
}
