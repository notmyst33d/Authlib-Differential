package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/request/ValidateRequest.class */
public class ValidateRequest {
    private String clientToken;
    private String accessToken;

    public ValidateRequest(YggdrasilUserAuthentication authenticationService) {
        this.clientToken = authenticationService.getAuthenticationService().getClientToken();
        this.accessToken = authenticationService.getAuthenticatedToken();
    }
}
