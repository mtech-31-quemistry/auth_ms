package com.quemistry.auth_ms.service;

import com.quemistry.auth_ms.config.RedisConfig;
import com.quemistry.auth_ms.config.RestClientConfig;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@WebMvcTest(AuthenticationService.class)
@ContextConfiguration(classes = {RestClientConfig.class, RedisConfig.class})
public class AuthenticationServiceImplTest {

    //@Value("${quemistry.cognito.url}")
    //private String QUEMISTRY_COGNITO_URL;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private TokenResponse tokenResponse;
    private UserProfile user;

    private String idToken;
    @BeforeEach
    void init(){
        user = new UserProfile();
        user.setEmail("testUser@email.com");
        user.setSessionId(UUID.randomUUID().toString());

        //idtoken with email set as testUser@email.com
        idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6InRlc3RVc2VyQGVtYWlsLmNvbSJ9.cI8ybuRu1FP7_jR9-mHuS2w9EBVueQRMR5DeF2C3pWc";
        tokenResponse = new TokenResponse();
        tokenResponse.setIdToken(idToken);
        tokenResponse.setAccessToken("testAccessToken");
        tokenResponse.setAccessToken("testRefreshToken");
        tokenResponse.setExpiresIn(120);
    }


   @Test
    void givenGetAccessToken_Success(){


        String tokenUri = "https://quemistry.auth.ap-southeast-1.amazoncognito.com/oauth2/token";
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientId("testclientId");
        tokenRequest.setAuthCode("testAuthCode");
        tokenRequest.setCodeVerifier("testCodeVerifier");
        tokenRequest.setRedirectUrl("testUrl");

       HttpHeaders headers = new HttpHeaders();
       headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());

       MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
       formData.add("grant_type","authorization_code");
       formData.add("redirect_uri",tokenRequest.getRedirectUrl());
       formData.add("client_id",tokenRequest.getClientId());
       formData.add("code",tokenRequest.getAuthCode());
       formData.add("code_verifier",tokenRequest.getCodeVerifier());
       HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formData, headers);

       Mockito.when(restTemplate.postForEntity(tokenUri, formEntity, TokenResponse.class))
               .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));

       Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        var result = authenticationService.getAccessToken(tokenRequest);
        tokenResponse.setEmail("testUser@email.com");
        Assertions.assertEquals(user.getEmail(), result.getEmail() );
    }

    @Test
    void givenSignOut_Success(){
        String revokeUri = "https://quemistry.auth.ap-southeast-1.amazoncognito.com/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id","testClientId");
        formData.add("token",tokenResponse.getRefreshToken());
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formData, headers);
        Mockito.when(restTemplate.postForEntity(revokeUri, formEntity, String.class))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK));
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForValue().getAndDelete(user.getSessionId()+"_tokens"))
                .thenReturn(tokenResponse);

        authenticationService.signOut(user.getSessionId(), "testClientId");
    }

}
