package com.quemistry.auth_ms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${quemistry.cognito.url}")
    private String QUEMISTRY_COGNITO_URL;

    @Override
    public TokenResponse getAccessToken(TokenRequest request) {
        final String tokenUri = QUEMISTRY_COGNITO_URL+"/oauth2/token";

        RestTemplate restTemplate = new RestTemplate();
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
                response.getBody().setEmail((String)map.get("email"));
            }
            catch (Exception ex) {
                System.err.println("Error reading id token in getAccessToken");
                System.err.println(ex.getStackTrace());
            }

            //store response in redis cache
            return response.getBody();
        }
        else{
            //log error
            return null;
        }
        //store token in redis cache
    }


}
