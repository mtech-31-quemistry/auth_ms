package com.quemistry.auth_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quemistry.auth_ms.model.*;
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

import static com.quemistry.auth_ms.constant.Auth.COOKIE_NAME;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void givenHealth_Success() throws Exception{
        mockMvc.perform(get("/v1/auth/health")).andExpect(status().isOk());
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
    void givenGetAccessToken_Fails() throws Exception{

        given(authenticationService.getAccessToken(tokenRequest)).willReturn(null);
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    @Test
    void givenSignOut_Success() throws Exception{
        SignOutRequest signOutRequest = new SignOutRequest();
        signOutRequest.setClientId("testClientId");

        doNothing().when(authenticationService).signOut(user.getSessionId(), signOutRequest.getClientId());
        ObjectMapper mapper = new ObjectMapper();

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(COOKIE_NAME,mapper.writeValueAsString(tokenResponse) );
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        var result = mockMvc.perform(post("/v1/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(mapper.writeValueAsString(signOutRequest)))
                .andExpect(status().isOk())
                .andReturn();
        var setCookieHeader = result.getResponse().getHeader("Set-Cookie");
        Assertions.assertNotNull(setCookieHeader);
    }

    @Test
    void givenisAuthorised_Success() throws Exception{
        var request = new IsAuthorisedRequest();
        request.setRole("tutor");
        request.setPath("/questions");
        request.setMethod("GET");

        ObjectMapper mapper = new ObjectMapper();

        given(authenticationService.checkAccess(request.getRole(), request.getPath(), request.getMethod()))
                .willReturn(true);

        var result = mockMvc.perform(post("/v1/auth/isauthorised")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("true", result.getResponse().getContentAsString());
    }

    @Test
    void givenisAuthorisedUser_Success() throws Exception{
        var request = new IsAuthorisedRequest();
        request.setRole("tutor");
        request.setPath("/questions");
        request.setMethod("GET");

        ObjectMapper mapper = new ObjectMapper();

        given(authenticationService.checkUserSessionAccess(request.getSessionId(), request.getPath(), request.getMethod()))
                .willReturn("userid-test");

        var result = mockMvc.perform(post("/v1/auth/isauthoriseduser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("userid-test", result.getResponse().getContentAsString());
    }
}
