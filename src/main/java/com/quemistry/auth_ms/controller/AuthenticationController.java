package com.quemistry.auth_ms.controller;

import com.quemistry.auth_ms.model.*;
import com.quemistry.auth_ms.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.quemistry.auth_ms.constant.Auth.COOKIE_NAME;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    @Value("${quemistry.session.timeout}")
    private int sessionTimeout;

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("health")
    public ResponseEntity<Object> health(){
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("service", "auth");
        responseBody.put("status", "UP");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    //exchange token request with access tokens from cognito
    @PostMapping()
    public ResponseEntity<UserProfile> getAccess(@RequestBody TokenRequest request){
        UserProfile userProfile = authenticationService.getAccessToken(request);

        if(userProfile == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        //create cookie and return code with cookie session
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, userProfile.getSessionId())
                        .httpOnly(true).secure(true)
                        .path("/").maxAge(sessionTimeout)
                        .sameSite(Cookie.SameSite.NONE.attributeValue())
                        .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

         return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userProfile);
    }

    @PostMapping("signout")
    public ResponseEntity<String> signOut(@CookieValue(COOKIE_NAME) String cookie, @RequestBody SignOutRequest signOutRequest){
        authenticationService.signOut(cookie, signOutRequest.getClientId());
        //expire cookie to remove from session
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie deleteCookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true).secure(true)
                .path("/").maxAge(0)
                .sameSite(Cookie.SameSite.NONE.attributeValue())
                .build();

        headers.add(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(null);
    }

    @PostMapping("isauthorised")
    public ResponseEntity<Boolean> isAuthorised(@RequestBody IsAuthorisedRequest request){
        var result =authenticationService.checkAccess(request.getRole(), request.getPath(), request.getMethod());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @PostMapping("isauthoriseduser")
    public ResponseEntity<IsAuthorisedUserResponse> isAuthorisedUser(@RequestBody IsAuthorisedRequest request){
        var result =authenticationService.checkUserSessionAccess(request.getSessionId(), request.getPath(), request.getMethod());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
