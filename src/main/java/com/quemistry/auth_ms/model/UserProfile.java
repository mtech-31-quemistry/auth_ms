package com.quemistry.auth_ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {
    private String sessionId;
    private String email;
    private String[] roles;
}
