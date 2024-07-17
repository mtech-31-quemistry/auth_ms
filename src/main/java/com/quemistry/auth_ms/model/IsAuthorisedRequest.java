package com.quemistry.auth_ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsAuthorisedRequest {

    private String role;
    private String path;
    private String method;
    private String sessionId;
}
