package com.quemistry.auth_ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    private String codeVerifier;
    private String authCode;
    private String redirectUrl;
    private String clientId;
}
