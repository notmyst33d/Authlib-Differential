package com.mojang.authlib.minecraft;

import com.mojang.authlib.HttpAuthenticationService;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/minecraft/HttpMinecraftSessionService.class */
public abstract class HttpMinecraftSessionService extends BaseMinecraftSessionService {
    /* JADX INFO: Access modifiers changed from: protected */
    public HttpMinecraftSessionService(HttpAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override // com.mojang.authlib.minecraft.BaseMinecraftSessionService
    public HttpAuthenticationService getAuthenticationService() {
        return (HttpAuthenticationService) super.getAuthenticationService();
    }
}
