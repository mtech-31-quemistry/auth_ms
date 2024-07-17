package com.quemistry.auth_ms.service;

import com.quemistry.auth_ms.model.TokenRequest;
import com.quemistry.auth_ms.model.UserProfile;

public interface AuthenticationService {
    UserProfile getAccessToken(TokenRequest request);

    void signOut(String sessionId, String clientId);

    Boolean checkAccess(String roleName, String path, String method);

    Boolean checkUserSessionAccess(String sessionId, String path, String method);
}
