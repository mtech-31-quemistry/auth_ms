package com.quemistry.auth_ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsAuthorisedUserResponse {
    private Boolean isAuthorised;
    private String userId;
}
