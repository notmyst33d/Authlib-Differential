package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/request/InvalidateRequest.class */
public class InvalidateRequest {
    private String accessToken;
    private String clientToken;

    public InvalidateRequest(YggdrasilUserAuthentication authenticationService) {
        this.accessToken = authenticationService.getAuthenticatedToken();
        this.clientToken = authenticationService.getAuthenticationService().getClientToken();
    }
}
