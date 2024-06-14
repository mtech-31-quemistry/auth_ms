package com.quemistry.auth_ms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

@WebMvcTest(AuthenticationService.class)
public class AuthenticationServiceImplTest {

    @Value("${quemistry.cognito.url}")
    private String QUEMISTRY_COGNITO_URL;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

   @Test
    void givenGetAccessToken_Success() throws Exception{
        TokenResponse tokenResponse = new TokenResponse();

        //idtoken with email set as testUser@email.com
        tokenResponse.setIdToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6InRlc3RVc2VyQGVtYWlsLmNvbSJ9.cI8ybuRu1FP7_jR9-mHuS2w9EBVueQRMR5DeF2C3pWc");

        String tokenUri = "https://quemistry.auth.ap-southeast-1.amazoncognito.com/oauth2/token";

        ObjectMapper mapper = new ObjectMapper();

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
               .thenReturn(new ResponseEntity(tokenResponse, HttpStatus.OK));

        var result = authenticationService.getAccessToken(tokenRequest);
        tokenResponse.setEmail("testUser@email.com");
        Assertions.assertEquals(tokenResponse, result );
    }
}
