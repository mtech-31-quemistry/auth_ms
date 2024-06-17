package com.quemistry.auth_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import com.quemistry.auth_ms.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    private TokenRequest tokenRequest;

    private TokenResponse tokenResponse;

    private UserProfile user;
    @BeforeEach
    void init(){
        tokenRequest = new TokenRequest();
        tokenRequest.setClientId("testclientId");
        tokenRequest.setAuthCode("testAuthCode");
        tokenRequest.setCodeVerifier("testCodeVerifier");
        tokenRequest.setRedirectUrl("testUrl");

        //output
        tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6InRlc3RVc2VyQGVtYWlsLmNvbSJ9.cI8ybuRu1FP7_jR9-mHuS2w9EBVueQRMR5DeF2C3pWc");
        tokenResponse.setEmail("testUser@email.com");

        user = new UserProfile();
        user.setEmail("testUser@email.com");
        user.setSessionId(UUID.randomUUID().toString());
    }
    @Test
    void givenGetAccessToken_Success() throws Exception{

        given(authenticationService.getAccessToken(tokenRequest)).willReturn(user);
        ObjectMapper mapper = new ObjectMapper();

        var result = mockMvc.perform(post("/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var setCookieHeader = result.getResponse().getHeader("Set-Cookie");
        Assertions.assertNotNull(setCookieHeader);

    }

    @Test
    void givenSignOut_Success() throws Exception{

        ObjectMapper mapper = new ObjectMapper();

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("QUESESSION",mapper.writeValueAsString(tokenResponse) );
        cookie.setHttpOnly(true);
        cookie.setPath("/");

            mockMvc.perform(post("/v1/auth/signout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(cookie))
                    .andExpect(status().isOk());
    }
}
