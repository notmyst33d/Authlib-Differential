package com.mojang.authlib;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/HttpUserAuthentication.class */
public abstract class HttpUserAuthentication extends BaseUserAuthentication {
    /* JADX INFO: Access modifiers changed from: protected */
    public HttpUserAuthentication(HttpAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override // com.mojang.authlib.BaseUserAuthentication
    public HttpAuthenticationService getAuthenticationService() {
        return (HttpAuthenticationService) super.getAuthenticationService();
    }
}
