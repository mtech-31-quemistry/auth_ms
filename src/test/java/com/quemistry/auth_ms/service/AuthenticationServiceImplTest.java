package com.quemistry.auth_ms.service;

import com.quemistry.auth_ms.config.RedisConfig;
import com.quemistry.auth_ms.config.RestClientConfig;
import com.quemistry.auth_ms.entity.ApiResource;
import com.quemistry.auth_ms.entity.Role;
import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import com.quemistry.auth_ms.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

//@WebMvcTest(AuthenticationService.class)
@ContextConfiguration(classes = {RestClientConfig.class, RedisConfig.class})
@SpringBootTest(properties = {
        "spring.data.redis.port=6379",
        "spring.data.redis.host=localhost",
        "spring.data.redis.ssl.enabled=false"})
public class AuthenticationServiceImplTest {

    //@Value("${quemistry.cognito.url}")
    //private String QUEMISTRY_COGNITO_URL;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private TokenResponse tokenResponse;
    private UserProfile user;

    private final String UserId = "c9aad54c-60e1-7045-e712-9ad1da73f87a";
    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {

        user = new UserProfile();
        user.setEmail("testUser@email.com");
        user.setRoles(new String[]{"tutor", "admin"});
        user.setSessionId(UUID.randomUUID().toString());

        //idtoken with email set as testUser@email.com
        String idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdF9oYXNoIjoidThRdnNKdFY1TXRETFdodF9xQmFpZyIsInN1YiI6ImM5YWFkNTRjLTYwZTEtNzA0NS1lNzEyLTlhZDFkYTczZjg3YSIsImNvZ25pdG86Z3JvdXBzIjpbInR1dG9yIiwiYWRtaW4iXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJpc3MiOiJodHRwczovL2NvZ25pdG8taWRwLmFwLXNvdXRoZWFzdC0xLmFtYXpvbmF3cy5jb20vYXAtc291dGhlYXN0LTFfWmIwSmwwN1dzIiwiY29nbml0bzp1c2VybmFtZSI6Imdvb2dsZV8xMDQwMjk0OTE2Njc5NjE1ODg4NDAiLCJvcmlnaW5fanRpIjoiYTY1M2ExMDEtNTUyMi00ZDIyLTk1NzctZGZkZjA4ZDM5NDc4IiwiYXVkIjoiMXEzMHZtZDB2Y2VlNmsxbHJwMmluMTA2MjMiLCJpZGVudGl0aWVzIjpbeyJkYXRlQ3JlYXRlZCI6IjE3MTcyNTI0Mzk3MDMiLCJ1c2VySWQiOiIxMDQwMjk0OTE2Njc5NjE1ODg4NDAiLCJwcm92aWRlck5hbWUiOiJHb29nbGUiLCJwcm92aWRlclR5cGUiOiJHb29nbGUiLCJpc3N1ZXIiOm51bGwsInByaW1hcnkiOiJ0cnVlIn1dLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTcyMTE0MDE3NywiZXhwIjoxNzIxMTQxMDc3LCJpYXQiOjE3MjExNDAxNzcsImp0aSI6IjU5YjlkZmZlLWFkMjItNDMyZC05ZWIxLTRiZmVhYjFhOGY4MyIsImVtYWlsIjoidGVzdFVzZXJAZW1haWwuY29tIn0.HabZEsulPCsu-IYRE_G42RUWo0k5jMJqYSxJx_QgtuY";

        tokenResponse = new TokenResponse();
        tokenResponse.setIdToken(idToken);
        tokenResponse.setAccessToken(idToken);
        tokenResponse.setRefreshToken("testRefreshToken");
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

       Assertions.assertEquals(user.getEmail(), result.getEmail() );
       Assertions.assertEquals(user.getRoles().length, result.getRoles().length );

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

    @Test
    void givenCheckAccess_Success(){
        Set<ApiResource> grantedWith = new HashSet<>();
        var apiResource = new ApiResource();
        apiResource.setPath("/questions");
        apiResource.setMethod("GET");

        grantedWith.add(apiResource);
        var role = new Role();
        role.setName("tutor");
        role.setGrantedWith(grantedWith);

        Mockito.when(roleRepository.findByName("tutor")).thenReturn(Optional.of(role));
        var result = authenticationService.checkAccess("tutor", "/questions", "GET");

        Assertions.assertEquals(result, true);
    }

    @Test
    void givenCheckAccess_DenyAccess(){
        Set<ApiResource> grantedWith = new HashSet<>();
        var apiResource = new ApiResource();
        apiResource.setPath("/questions");
        apiResource.setMethod("GET");

        grantedWith.add(apiResource);
        var role = new Role();
        role.setName("tutor");
        role.setGrantedWith(grantedWith);

        Mockito.when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        var result = authenticationService.checkAccess("tutor", "/questions", "POST");

        Assertions.assertEquals(result, false);
    }

    @Test
    void givencheckUserSessionAccess_Success(){
        Set<ApiResource> grantedWith = new HashSet<>();
        var apiResource = new ApiResource();
        apiResource.setPath("/questions");
        apiResource.setMethod("GET");

        grantedWith.add(apiResource);
        var role = new Role();
        role.setName("tutor");
        role.setGrantedWith(grantedWith);

        var roles = new ArrayList<Role>();
        roles.add(role);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_profile"))
                .thenReturn(user);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_tokens"))
                .thenReturn(tokenResponse);

        Mockito.when(roleRepository.findByNames(user.getRoles())).thenReturn(roles);
        var result = authenticationService.checkUserSessionAccess(user.getSessionId(), "/questions", "GET");

        Assertions.assertEquals(UserId, result.getUserId() );
        Assertions.assertEquals(true, result.getIsAuthorised() );
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Assertions.assertEquals(String.join("|", user.getRoles()), result.getRoles());
    }

    @Test
    void givencheckUserSessionAccess_InValidSession(){
        Set<ApiResource> grantedWith = new HashSet<>();
        var apiResource = new ApiResource();
        apiResource.setPath("/questions");
        apiResource.setMethod("GET");

        grantedWith.add(apiResource);
        var role = new Role();
        role.setName("tutor");
        role.setGrantedWith(grantedWith);

        var roles = new ArrayList<Role>();
        roles.add(role);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_profile"))
                .thenReturn(null);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_tokens"))
                .thenReturn(null);

        Mockito.when(roleRepository.findByNames(user.getRoles())).thenReturn(roles);
        var result = authenticationService.checkUserSessionAccess(user.getSessionId(), "/questions", "GET");

        Assertions.assertEquals(false, result.getIsAuthorised());
        Assertions.assertEquals(null, result.getUserId());

    }

    @Test
    void givencheckUserSessionAccess_NoAccess(){
        Set<ApiResource> grantedWith = new HashSet<>();
        var apiResource = new ApiResource();
        apiResource.setPath("/questions");
        apiResource.setMethod("GET");

        grantedWith.add(apiResource);
        var role = new Role();
        role.setName("tutor");
        role.setGrantedWith(grantedWith);

        var roles = new ArrayList<Role>();
        roles.add(role);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_profile"))
                .thenReturn(user);
        Mockito.when(redisTemplate.opsForValue().get(user.getSessionId()+"_tokens"))
                .thenReturn(tokenResponse);

        Mockito.when(roleRepository.findByNames(user.getRoles())).thenReturn(roles);
        var result = authenticationService.checkUserSessionAccess(user.getSessionId(), "/questions", "POST");

        Assertions.assertEquals(UserId, result.getUserId());
        Assertions.assertEquals(false, result.getIsAuthorised());

    }
}
