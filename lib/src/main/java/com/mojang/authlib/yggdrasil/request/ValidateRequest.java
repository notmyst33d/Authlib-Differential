package com.mojang.authlib.yggdrasil.request;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/request/ValidateRequest.class */
public class ValidateRequest {
    private String clientToken;
    private String accessToken;

    public ValidateRequest(String accessToken, String clientToken) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
    }
}
