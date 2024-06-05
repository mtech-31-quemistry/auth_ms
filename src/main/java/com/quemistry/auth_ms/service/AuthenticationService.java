package com.quemistry.auth_ms.service;

import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(TokenRequest request);
}
