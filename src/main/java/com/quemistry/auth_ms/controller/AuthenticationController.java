package com.quemistry.auth_ms.controller;

import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;
import com.quemistry.auth_ms.model.UserProfile;
import com.quemistry.auth_ms.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping()
    public ResponseEntity<UserProfile> getAccess(@RequestBody TokenRequest request){
        TokenResponse tokenResponse = authenticationService.getAccessToken(request);

         //create cookie and return code with response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", String.format("quemistry_session=%s; Max-Age=86400; Path=/; HttpOnly",tokenResponse.getAccessToken()));

        if(tokenResponse == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        var user = new UserProfile();
        user.setEmail(tokenResponse.getEmail());

         return ResponseEntity.status(HttpStatus.OK).headers(headers).body(user);
    }
}
